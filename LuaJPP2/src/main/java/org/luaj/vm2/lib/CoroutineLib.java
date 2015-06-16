/*******************************************************************************
 * Copyright (c) 2007-2011 LuaJ. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.lib.J2sePlatform;

/**
 * Subclass of {@link LibFunction} which implements the lua standard
 * {@code coroutine} library.
 * <p>
 * The coroutine library in luaj has the same behavior as the coroutine library
 * in C, but is implemented using Java Threads to maintain the call state
 * between invocations. Therefore it can be yielded from anywhere, similar to
 * the "Coco" yield-from-anywhere patch available for C-based lua. However,
 * coroutines that are yielded but never resumed to complete their execution may
 * not be collected by the garbage collector.
 * <p>
 * Typically, this library is included as part of a call to either
 * {@link J2sePlatform#standardGlobals()} or
 * {@code JmePlatform.standardGlobals()}
 * <p>
 * To instantiate and use it directly, link it into your globals table via
 * {@link LuaValue#load(LuaValue)} using code such as:
 *
 * <pre>
 * {
 * 	&#064;code
 * 	LuaTable _G = new LuaTable();
 * 	_G.load(new CoroutineLib());
 * }
 * </pre>
 *
 * Doing so will ensure the library is properly initialized and loaded into the
 * globals table.
 * <p>
 *
 * @see LibFunction
 * @see J2sePlatform
 * @see <a
 *      href="http://www.lua.org/manual/5.1/manual.html#5.2">http://www.lua.org/manual/5.1/manual.html#5.2</a>
 */
@LuaSerializable
public class CoroutineLib extends VarArgFunction {

	private static final long serialVersionUID = 7212992065052827003L;

	private static final int INIT = 0;
	private static final int CREATE = 1;
	private static final int RESUME = 2;
	private static final int RUNNING = 3;
	private static final int STATUS = 4;
	private static final int YIELD = 5;
	private static final int WRAP = 6;
	private static final int WRAPPED = 7;

	public CoroutineLib() {
	}

	private LuaTable init() {
		LuaTable t = new LuaTable();
		bind(t, CoroutineLib.class,
				new String[] { "create", "resume", "running", "status", "yield", "wrap" }, CREATE);
		env.set("coroutine", t);
		PackageLib.getCurrent().LOADED.set("coroutine", t);
		return t;
	}

	@Override
	public Varargs invoke(Varargs args) {
		switch (opcode) {
		case INIT: {
			return init();
		}
		case CREATE: {
			final LuaClosure func = args.checkclosure(1);
			final LuaThread running = LuaThread.getRunning();
			return new LuaThread(running, func);
		}
		case RESUME: {
			final LuaThread t = args.checkthread(1);
			return t.resume(-1);
		}
		case RUNNING: {
			return LuaThread.getRunning();
		}
		case STATUS: {
			return valueOf(args.checkthread(1).getStatus());
		}
		case YIELD: {
			final LuaThread running = LuaThread.getRunning();
			return running.yield(args);
		}
		case WRAP: {
			final LuaClosure func = args.checkclosure(1);
			final LuaThread running = LuaThread.getRunning();
			final LuaThread thread = new LuaThread(running, func);
			thread.setfenv(func.getfenv());
			CoroutineLib cl = new CoroutineLib();
			cl.setfenv(thread);
			cl.name = "wrapped";
			cl.opcode = WRAPPED;
			return cl;
		}
		case WRAPPED: {
			final LuaThread t = (LuaThread) env;
			final Varargs result = t.resume(-1);
			if (t.isDead()) {
                throw new LuaError(result.arg1().tojstring());
			} else {
				return result;
			}
		}
		default:
			return NONE;
		}
	}
}
