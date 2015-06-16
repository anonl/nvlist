package nl.weeaboo.vn.script.lua;

import nl.weeaboo.lua2.LuaException;

public interface ILuaScriptEnvInitializer {

    public void initEnv(LuaScriptEnv env) throws LuaException;

}
