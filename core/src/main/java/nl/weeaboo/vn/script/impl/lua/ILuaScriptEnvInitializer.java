package nl.weeaboo.vn.script.impl.lua;

import nl.weeaboo.vn.script.ScriptException;

public interface ILuaScriptEnvInitializer {

    void initEnv(LuaScriptEnv env) throws ScriptException;

}
