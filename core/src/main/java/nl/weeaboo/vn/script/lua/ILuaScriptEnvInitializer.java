package nl.weeaboo.vn.script.lua;

import nl.weeaboo.vn.script.ScriptException;

public interface ILuaScriptEnvInitializer {

    public void initEnv(LuaScriptEnv env) throws ScriptException;

}
