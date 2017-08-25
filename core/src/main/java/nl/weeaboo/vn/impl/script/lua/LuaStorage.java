package nl.weeaboo.vn.impl.script.lua;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.lua2.vm.LuaBoolean;
import nl.weeaboo.lua2.vm.LuaDouble;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.StoragePrimitive;

/** Lua wrapper around {@link IStorage} */
public final class LuaStorage implements ILuaStorage {

    private static final long serialVersionUID = 1L;

    private final IStorage storage;

    private LuaStorage(IStorage storage) {
        this.storage = Checks.checkNotNull(storage);
    }

    /** Converts {@link IStorage} to {@link ILuaStorage}. */
    public static ILuaStorage from(IStorage storage) {
        return new LuaStorage(storage);
    }

    @Override
    public LuaValue get(String key) {
        return storageToLua(storage.get(key));
    }

    @Override
    public void set(String key, LuaValue val) {
        storage.set(key, luaToStorage(val));
    }

    /** Converts a Lua value to its equivalent {@link StoragePrimitive}. */
    public static @Nullable StoragePrimitive luaToStorage(LuaValue lval) {
        if (lval.isnil()) {
            return null;
        } else if (lval.isboolean()) {
            return StoragePrimitive.fromBoolean(lval.toboolean());
        } else if (lval.isnumber()) {
            return StoragePrimitive.fromDouble(lval.todouble());
        } else if (lval.isstring()) {
            return StoragePrimitive.fromString(lval.tojstring());
        }
        throw new IllegalArgumentException("Unable to convert Lua type for storage: " + lval.typename());
    }

    /** Converts a {@link StoragePrimitive} to its equivalent Lua value. */
    public static LuaValue storageToLua(@Nullable StoragePrimitive sval) {
        if (sval == null) {
            return LuaNil.NIL;
        } else if (sval.isBoolean()) {
            return LuaBoolean.valueOf(sval.toBoolean(false));
        } else if (sval.isDouble()) {
            return LuaDouble.valueOf(sval.toDouble(0.0));
        } else if (sval.isString()) {
            return LuaString.valueOf(sval.toString(""));
        }
        throw new IllegalArgumentException("Unable to convert storage primitive to Lua: " + sval.toJson());
    }

}
