package nl.weeaboo.vn.impl.script.lua;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.lua2.vm.LuaBoolean;
import nl.weeaboo.lua2.vm.LuaDouble;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.vn.impl.save.Storage;
import nl.weeaboo.vn.save.StoragePrimitive;

public final class LuaStorageTest {

    private final ExceptionTester exTester = new ExceptionTester();

    private Storage backing;
    private ILuaStorage luaStorage;

    @Before
    public void before() {
        backing = new Storage();
        backing.set("nil", null);
        backing.setBoolean("boolean", true);
        backing.setDouble("number", 12.5);
        backing.setString("string", "test");
        luaStorage = LuaStorage.from(backing);
    }

    @Test
    public void testNil() {
        assertGet("nil", LuaNil.NIL);
        assertSet(LuaNil.NIL, null);
    }

    @Test
    public void testBoolean() {
        assertGet("boolean", LuaBoolean.TRUE);
        assertSet(LuaBoolean.TRUE, StoragePrimitive.fromBoolean(true));
    }

    @Test
    public void testNumber() {
        assertGet("number", LuaDouble.valueOf(12.5));
        assertSet(LuaDouble.valueOf(0.5), StoragePrimitive.fromDouble(0.5));
    }

    @Test
    public void testString() {
        assertGet("string", LuaString.valueOf("test"));
        assertSet(LuaString.valueOf("123"), StoragePrimitive.fromString("123"));
    }

    @Test
    public void testTable() {
        // Table values aren't supported
        exTester.expect(IllegalArgumentException.class, () -> {
            luaStorage.set("lua", new LuaTable());
        });
    }

    private void assertGet(String key, LuaValue expected) {
        Assert.assertEquals(expected, luaStorage.get(key));
    }

    private void assertSet(LuaValue value, StoragePrimitive expected) {
        luaStorage.set("lua", value);
        Assert.assertEquals(expected, backing.get("lua"));
    }

}
