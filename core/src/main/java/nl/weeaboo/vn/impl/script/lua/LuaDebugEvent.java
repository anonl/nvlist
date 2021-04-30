package nl.weeaboo.vn.impl.script.lua;

/**
 * Lua debug event type.
 *
 * @see ILuaDebugHook
 */
public enum LuaDebugEvent {
    UNKNOWN,
    CALL,
    RETURN,
    TAIL_RETURN,
    LINE,
    COUNT;

    public static LuaDebugEvent of(String eventName) {
        switch (eventName) {
        case "call":
            return CALL;
        case "return":
            return LuaDebugEvent.RETURN;
        case "tail return":
            return LuaDebugEvent.TAIL_RETURN;
        case "line":
            return LINE;
        case "count":
            return COUNT;
        default:
            return UNKNOWN;
        }
    }

}
