package nl.weeaboo.vn.impl.script.lua;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.input.INativeInput;

/**
 * Interactive Lua terminal/REPL
 */
public interface ILuaConsole {

    /** Handle input and update internal state. */
    void update(IEnvironment env, INativeInput input);

}
