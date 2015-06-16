/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
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

import org.luaj.vm2.LoadState.LuaCompiler;
import org.luaj.vm2.compiler.LuaC;

import nl.weeaboo.lua2.io.LuaSerializable;

/**
 * Extension of {@link LuaFunction} which executes lua bytecode.
 * <p>
 * A {@link LuaClosure} is a combination of a {@link Prototype} and a
 * {@link LuaValue} to use as an environment for execution.
 * <p>
 * There are three main ways {@link LuaClosure} instances are created:
 * <ul>
 * <li>Construct an instance using {@link #LuaClosure(Prototype, LuaValue)}</li>
 * <li>Construct it indirectly by loading a chunk via
 * {@link LuaCompiler#load(java.io.InputStream, String, LuaValue)}
 * <li>Execute the lua bytecode {@link Lua#OP_CLOSURE} as part of bytecode
 * processing
 * </ul>
 * <p>
 * To construct it directly, the {@link Prototype} is typically created via a
 * compiler such as {@link LuaC}:
 *
 * <pre>
 * {@code
 * InputStream is = new ByteArrayInputStream("print('hello,world').getBytes());
 * Prototype p = LuaC.instance.compile(is, "script");
 * LuaValue _G = JsePlatform.standardGlobals()
 * LuaClosure f = new LuaClosure(p, _G);
 * }
 * </pre>
 * <p>
 * To construct it indirectly, the {@link LuaC} compiler may be used, which
 * implements the {@link LuaCompiler} interface:
 *
 * <pre>
 * {
 * 	&#064;code
 * 	LuaFunction f = LuaC.instance.load(is, &quot;script&quot;, _G);
 * }
 * </pre>
 * <p>
 * Typically, a closure that has just been loaded needs to be initialized by
 * executing it, and its return value can be saved if needed:
 *
 * <pre>
 * {@code
 * LuaValue r = f.call();
 * _G.set( "mypkg", r )
 * }
 * </pre>
 * <p>
 * In the preceding, the loaded value is typed as {@link LuaFunction} to allow
 * for the possibility of other compilers such as LuaJC producing
 * {@link LuaFunction} directly without creating a {@link Prototype} or
 * {@link LuaClosure}.
 * <p>
 * Since a {@link LuaClosure} is a {@link LuaFunction} which is a
 * {@link LuaValue}, all the value operations can be used directly such as:
 * <ul>
 * <li>{@link LuaValue#setfenv(LuaValue)}</li>
 * <li>{@link LuaValue#call()}</li>
 * <li>{@link LuaValue#call(LuaValue)}</li>
 * <li>{@link LuaValue#invoke()}</li>
 * <li>{@link LuaValue#invoke(Varargs)}</li>
 * <li>{@link LuaValue#method(String)}</li>
 * <li>{@link LuaValue#method(String,LuaValue)}</li>
 * <li>{@link LuaValue#invokemethod(String)}</li>
 * <li>{@link LuaValue#invokemethod(String,Varargs)}</li>
 * <li>...</li>
 * </ul>
 *
 * @see LuaValue
 * @see LuaFunction
 * @see LuaValue#isclosure()
 * @see LuaValue#checkclosure()
 * @see LuaValue#optclosure(LuaClosure)
 * @see LoadState
 * @see LoadState#compiler
 */
@LuaSerializable
public final class LuaClosure extends LuaFunction {

	private static final long serialVersionUID = 1884693136872518784L;

	private static final UpValue[] NOUPVALUES = new UpValue[0];

	private Prototype p;
	private UpValue[] upValues;

	/** Supply the initial environment */
	public LuaClosure(Prototype p, LuaValue env) {
		super(env);

		this.p = p;
		this.upValues = (p.nups > 0 ? new UpValue[p.nups] : NOUPVALUES);
	}

	protected LuaClosure(int nupvalues, LuaValue env) {
		super(env);

		this.p = null;
		this.upValues = (nupvalues > 0 ? new UpValue[nupvalues] : NOUPVALUES);
	}

	@Override
	public boolean isclosure() {
		return true;
	}

	@Override
	public LuaClosure optclosure(LuaClosure defval) {
		return this;
	}

	@Override
	public LuaClosure checkclosure() {
		return this;
	}

	@Override
	public final LuaValue call() {
		return invoke(NONE).arg1();
	}

	@Override
	public final LuaValue call(LuaValue arg) {
		return invoke(arg).arg1();
	}

	@Override
	public final LuaValue call(LuaValue arg1, LuaValue arg2) {
		return invoke(varargsOf(arg1, arg2)).arg1();
	}

	@Override
	public final LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
		return invoke(varargsOf(arg1, arg2, arg3)).arg1();
	}

	@Override
	public final Varargs invoke(Varargs varargs) {
		return LuaThread.execute(this, varargs);
	}

	public Prototype getPrototype() {
		return p;
	}
	public UpValue[] getUpValues() {
		return upValues;
	}

	public LuaValue getUpvalue(int i) {
		return upValues[i].getValue();
	}
	public int getUpValueCount() {
		return upValues.length;
	}

	public void setUpvalue(int i, LuaValue v) {
		upValues[i].setValue(v);
	}

	@Override
	public String tojstring() {
		return type() + " " + p.source+":"+p.linedefined+"-"+p.lastlinedefined;
	}

}
