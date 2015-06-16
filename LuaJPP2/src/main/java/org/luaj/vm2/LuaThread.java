/*******************************************************************************
 * Copyright (c) 2007 LuaJ. All rights reserved.
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
package org.luaj.vm2;

import java.io.Serializable;

import org.luaj.vm2.lib.DebugLib;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.interpreter.LuaInterpreter;
import nl.weeaboo.lua2.interpreter.StackFrame;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public final class LuaThread extends LuaValue implements Serializable {

	private static final long serialVersionUID = -5915771649511060629L;

	private static final String[] STATUS_NAMES = { "suspended", "running", "normal", "dead", "endcall" };
	public static final int STATUS_SUSPENDED = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_NORMAL = 2;
	public static final int STATUS_DEAD = 3;
	public static final int STATUS_ERROR = 4;
	public static final int STATUS_END_CALL = 5;

	public static final int MAX_CALLSTACK = 128;
	public static LuaValue s_metatable;

	private LuaRunState luaRunState;
	private LuaValue env;
	private int status = STATUS_SUSPENDED;
	private int callstackMin;
	private boolean isMainThread;
	public StackFrame callstack;
	public Object debugState;

	/**
	 * Do not use. Required for efficient serialization.
	 */
	@Deprecated
	public LuaThread() {
	}

	public LuaThread(LuaRunState lrs, LuaValue environment) {
		luaRunState = lrs;
		env = environment;

		callstack = null;
	}

	/**
	 * Create a LuaThread around a function and environment
	 *
	 * @param function The function to execute
	 */
	public LuaThread(LuaThread parent, LuaClosure function) {
		this(parent.luaRunState, parent.getfenv());

		callstack = StackFrame.newInstance(function, NONE, null, 0, 0);
	}

	public static LuaThread createMainThread(LuaRunState lrs, LuaValue env) {
		LuaThread thread = new LuaThread(lrs, env);
		thread.isMainThread = true;
		return thread;
	}

	@Override
	public String toString() {
		return (isMainThread ? "main" : "") + super.toString();
	}

	@Override
	public int type() {
		return LuaValue.TTHREAD;
	}

	@Override
	public String typename() {
		return "thread";
	}

	@Override
	public boolean isthread() {
		return true;
	}

	@Override
	public LuaThread optthread(LuaThread defval) {
		return this;
	}

	@Override
	public LuaThread checkthread() {
		return this;
	}

	@Override
	public LuaValue getmetatable() {
		return s_metatable;
	}

	@Override
	public LuaValue getfenv() {
		return env;
	}

	@Override
	public void setfenv(LuaValue env) {
		this.env = env;
	}

	public boolean isRunning() {
		return (isMainThread && !isDead()) || status == STATUS_RUNNING;
	}
	public boolean isFinished() {
		return isDead() || callstack == null;
	}
	public boolean isDead() {
		return status == STATUS_DEAD;
	}
	public boolean isEndCall() {
		return status == STATUS_END_CALL;
	}

	public String getStatus() {
		return STATUS_NAMES[status];
	}

	public void destroy() {
		status = STATUS_DEAD;
	}

	public int callstackSize() {
		return (callstack != null ? callstack.size() : 0);
	}

	public void preCall(StackFrame sf, int calls) {
		if (DebugLib.DEBUG_ENABLED) {
			DebugLib.debugOnCall(this, calls, sf.getCallstackFunction(0));
			//System.out.println(">>" + calls);
		}
	}

    /**
     * @param sf The stack frame that was just poppep from the callstack.
     */
	public void postReturn(StackFrame sf, int calls) {
		if (DebugLib.DEBUG_ENABLED) {
			DebugLib.debugOnReturn(this, calls);
			//System.out.println("<<" + calls);
		}
	}

	public void pushPending(LuaClosure func, Varargs args) {
		pushPending(func, args, -1, 0);
	}
	public void pushPending(LuaClosure func, Varargs args, int returnBase, int returnCount) {
		callstack = StackFrame.newInstance(func, args, callstack, returnBase, returnCount);
	}

	public LuaFunction getCallstackFunction(int level) {
		return callstack.getCallstackFunction(level);
	}

	public static LuaThread getRunning() {
		LuaRunState lrs = LuaRunState.getCurrent();
		if (lrs == null) {
			throw new RuntimeException("No LuaRunState valid on current thread: " + Thread.currentThread());
		}
		return lrs.getRunningThread();
	}

	/**
	 * Yield this thread with arguments
	 */
	public Varargs yield(Varargs args) {
		if (!isRunning()) {
			error(this + " not running");
		} else if (isMainThread) {
			error("Main thread can't yield");
		} /*else if (puritySwitches > 1) {
			error(this + " cannot yield when called through non-Lua code");
		}*/

		status = STATUS_SUSPENDED;
		return args;
	}

	public Varargs endCall(Varargs args) {
		args = yield(args);
		status = STATUS_END_CALL;
		return args;
	}

	public Varargs resume(int maxDepth) {
		if (isDead()) {
			return valueOf("cannot resume dead thread");
		}

		Varargs result;

		final int oldCallstackMin = callstackMin;
		final LuaThread prior = luaRunState.getRunningThread();
		try {
			if (prior.status == STATUS_RUNNING) {
				prior.status = STATUS_NORMAL;
			}
			status = STATUS_RUNNING;
			luaRunState.setRunningThread(this);

			callstackMin = Math.max(callstackMin, (maxDepth < 0 ? 0 : callstackSize() - maxDepth));
			result = LuaInterpreter.resume(this, callstackMin);
		} catch (LuaError e) {
			throw e;
		} catch (Throwable t) {
			throw new LuaError("Runtime error :: " + t, t);
		} finally {
			callstackMin = oldCallstackMin;
			if (status == STATUS_RUNNING) {
				status = STATUS_NORMAL;
			}
			if (prior.status == STATUS_NORMAL) {
				prior.status = STATUS_RUNNING;
			}
			luaRunState.setRunningThread(prior);
		}

		return result;
	}

	public static Varargs execute(LuaClosure c, Varargs args) {
		LuaThread running = getRunning();
		running.pushPending(c, args);
		return running.resume(1);
	}

}
