package org.luaj.vm2;

import org.luaj.vm2.lib.DebugLib;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class LuaError extends RuntimeException {

	private static final long serialVersionUID = 1657137595545123245L;

	private static final String DEFAULT_MESSAGE = "Lua error";
	private static final int MAX_LEVELS = 8;
	
	private String message;
	private Throwable cause;

	public LuaError() {
		this(DEFAULT_MESSAGE, null, 0);
	}
	public LuaError(String message) {
		this(message, null, 0);
	}
	public LuaError(Throwable c) {
		this(DEFAULT_MESSAGE, c, 0);
	}
	public LuaError(String message, Throwable c) {
		this(message, c, 0);
	}
	public LuaError(String message, Throwable c, int level) {
		super(message, c);
		
		this.message = message;
		this.cause = c;
		
		if (level >= 0) {
			StackTraceElement[] stack = DebugLib.stackTrace(LuaThread.getRunning(), level, MAX_LEVELS);
			if (c != null) {
				stack = prefixLuaStackTrace(c, stack);
			} else {
				stack = prefixLuaStackTrace(this, stack);
			}
			setStackTrace(stack);
		}		
	}
	public LuaError(LuaError lee) {
		this(lee.message, lee.cause, -1);
		
		setStackTrace(lee.getStackTrace());		
	}

	//Functions
	private static StackTraceElement[] prefixLuaStackTrace(Throwable t, StackTraceElement[] luaStack) {
		StackTraceElement[] javaStack = t.getStackTrace();		
		StackTraceElement[] newStack = new StackTraceElement[luaStack.length + javaStack.length];
		
		int joff = 0;
		for (int n = 0; n < javaStack.length; n++) {
			if (javaStack[n].getClassName().contains("LuaInterpreter")
					&& javaStack[n].getMethodName().equals("resume"))
			{
				joff = n;
				break;
			}
		}
		System.arraycopy(javaStack, 0, newStack, 0, joff);
		System.arraycopy(luaStack, 0, newStack, joff, luaStack.length);
		System.arraycopy(javaStack, joff, newStack, joff+luaStack.length, javaStack.length-joff);
		return newStack;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

}
