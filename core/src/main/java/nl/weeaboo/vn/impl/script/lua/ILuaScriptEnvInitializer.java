package nl.weeaboo.vn.impl.script.lua;

import java.io.Serializable;

import nl.weeaboo.vn.script.ScriptException;

public interface ILuaScriptEnvInitializer extends Serializable {

    void initEnv(LuaScriptEnv env) throws ScriptException;

}
