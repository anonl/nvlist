package nl.weeaboo.vn.impl.script.lua;

import java.io.Serializable;

import nl.weeaboo.lua2.vm.LuaValue;

public interface ILuaStorage extends Serializable {

    /**
     * Fetches a previously stored Lua value.
     * @return The stored Lua value, or {@code LuaNil#NIL} if no values was stored under the given key.
     */
    LuaValue get(String key);

    /** Stores a Lua value under the given key, overwriting any previous value for that key. */
    void set(String key, LuaValue val);

}
