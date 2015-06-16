package nl.weeaboo.lua2.lib;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public abstract class LuaLibrary extends VarArgFunction {

	private static final long serialVersionUID = -46607383638064637L;

	public LuaLibrary() {
	}

	//Functions
	@Override
	public Varargs invoke(Varargs args) {
		throw new LuaError("Invalid function id: " + opcode);
	}

	protected abstract LuaLibrary newInstance();

	private void init(int opcode, String name, LuaValue env) {
		this.opcode = opcode;
		this.name = name;
		this.env = env;
	}

	protected Varargs initLibrary(String tableName, String[] names, int opcodeOffset) {
		LuaTable t = new LuaTable();
		try {
			for (int i = 0, n = names.length; i < n; i++) {
				LuaLibrary f = newInstance();
				f.init(opcodeOffset + i, names[i], env);
				t.set(names[i], f);
			}
		} catch (Exception e) {
			throw new LuaError("bind failed: " + e);
		}
		env.set(tableName, t);
		return t;
	}

	//Getters

	//Setters

}
