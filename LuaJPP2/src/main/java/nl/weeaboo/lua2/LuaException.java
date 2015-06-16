package nl.weeaboo.lua2;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class LuaException extends Exception {

	private static final long serialVersionUID = -2104847632133502379L;

	public LuaException(String message, Throwable cause) {
		super(message, cause);
	}

	public LuaException(String message) {
		super(message);
	}

	public LuaException(Throwable cause) {
		super(cause);
	}
	
}
