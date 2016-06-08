package nl.weeaboo.vn.test.integration;

import org.junit.Test;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.script.impl.lua.LuaTestUtil;

public class LuaTextTest extends LuaIntegrationTest {

    @Test
    public void testStringifiers() {
        loadScript("integration/text/stringifiers");
        new StyledText("test").length();

        LuaTestUtil.assertGlobal("resultApple", new StyledText("banana"));
        LuaTestUtil.assertGlobal("resultCherry", new StyledText("durian"));
        LuaTestUtil.assertGlobal("resultInt", new StyledText("123"));
        LuaTestUtil.assertGlobal("resultFloat", new StyledText("2.5"));
        LuaTestUtil.assertGlobal("resultFuncnil", null);
        LuaTestUtil.assertGlobal("resultFuncnone", null);
    }

}
