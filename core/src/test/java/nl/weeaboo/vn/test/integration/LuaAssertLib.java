package nl.weeaboo.vn.test.integration;

import org.junit.Assert;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lib.LuaLib;

public class LuaAssertLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    public LuaAssertLib() {
        super(null); // Register functions in global scope
    }

    @ScriptFunction
    public void luaAssert(Varargs args) {
        if (!args.checkboolean(1)) {
            throw new AssertionError("Assert failed: " + args);
        }
    }

    @ScriptFunction
    public void luaAssertEquals(Varargs args) {
        LuaValue expected = args.arg(1);
        LuaValue actual = args.arg(2);

        String message = StringUtil.formatRoot("Expected '%s', Actual: '%s'", expected, actual);
        Assert.assertTrue(message, expected.eq_b(actual));
    }

}
