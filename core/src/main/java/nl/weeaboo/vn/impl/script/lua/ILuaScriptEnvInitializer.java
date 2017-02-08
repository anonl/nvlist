package nl.weeaboo.vn.impl.script.lua;

import java.io.Serializable;

import nl.weeaboo.vn.script.ScriptException;

public interface ILuaScriptEnvInitializer extends Serializable {

    /**
     * Initializes part of the Lua scripting environment.
     *
     * @throws ScriptException If the initialization code throws an exception.
     */
    void initEnv(LuaScriptEnv env) throws ScriptException;

}
