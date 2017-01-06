package nl.weeaboo.vn.script.impl.lua;

import java.io.Serializable;

import nl.weeaboo.lua2.vm.LuaValue;

public interface ILuaStorage extends Serializable {

    LuaValue get(String key);

    void set(String key, LuaValue val);

}
