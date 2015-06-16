package nl.weeaboo.lua2.link;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class LuaMethodLink extends LuaFunctionLink {

	private static final long serialVersionUID = -4961885811090442757L;

	public final LuaUserdata self;

	public LuaMethodLink(LuaRunState lrs, LuaUserdata self, String methodName, Object... args) {
		super(lrs, methodName, args);

		this.self = self;
	}

	public LuaMethodLink(LuaRunState lrs, LuaUserdata self, LuaClosure func, Varargs args) {
		super(lrs, func, args);

		this.self = self;
	}

	//Functions
	@Override
	protected Varargs getImplicitArgs() {
		return self;
	}

	@Override
	protected LuaClosure getFunction(String methodName) {
		LuaValue val = self.get(LuaString.valueOf(methodName));
		return (val instanceof LuaClosure ? (LuaClosure)val : null);
	}

	//Getters

	//Setters

}
