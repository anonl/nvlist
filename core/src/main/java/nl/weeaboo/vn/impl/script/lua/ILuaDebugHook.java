package nl.weeaboo.vn.impl.script.lua;

/**
 * Lua debug library callback.
 */
public interface ILuaDebugHook {

    void onEvent(LuaDebugEvent event, int lineNumber);

}