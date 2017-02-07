package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Assert;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.script.lib.LuaLib;
import nl.weeaboo.vn.script.ScriptFunction;

public class LuaAssertLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    public LuaAssertLib() {
        super(null); // Register functions in global scope
    }

    /**
     * Throws an assertion error if the boolean argument is {@code false}.
     *
     * @param args
     *        <ol>
     *        <li>boolean
     *        </ol>
     */
    @ScriptFunction
    public void luaAssert(Varargs args) {
        if (!args.checkboolean(1)) {
            throw new AssertionError("Assert failed: " + args);
        }
    }

    /**
     * Throws an assertion error if the two values aren't equal according to Lua's equality operator.
     *
     * @param args
     *        <ol>
     *        <li>expected value
     *        <li>actual value
     *        </ol>
     */
    @ScriptFunction
    public void luaAssertEquals(Varargs args) {
        LuaValue expected = args.arg(1);
        LuaValue actual = args.arg(2);

        String message = StringUtil.formatRoot("Expected '%s', Actual: '%s'", expected, actual);
        Assert.assertTrue(message, expected.eq_b(actual));
    }

}
