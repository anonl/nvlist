package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.styledtext.StyleParseException;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.script.lib.BasicTagHandler;
import nl.weeaboo.vn.impl.script.lib.TextLib;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.scene.ITextDrawable;

public class TextLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new TextLib(env, scriptEnv));
    }

    @Test
    public void createStyle() throws StyleParseException {
        loadScript("text/createStyle");

        LuaTestUtil.assertGlobal("simple",
                TextStyle.fromString("fontName=a|fontSize=13.24"));
        LuaTestUtil.assertGlobal("complex1",
                TextStyle.fromString("fontName=b|fontStyle=italic|color=AABBCCDD"));
    }

    @Test
    public void createTextDrawable() {
        loadScript("text/createTextDrawable");

        ITextDrawable fullDefault = LuaTestUtil.getGlobal("fullDefault", ITextDrawable.class);
        Assert.assertNotNull(fullDefault);

        ITextDrawable plainText = LuaTestUtil.getGlobal("plainText", ITextDrawable.class);
        Assert.assertEquals(new StyledText("abc"), plainText.getText());

        ITextDrawable styledText = LuaTestUtil.getGlobal("styledText", ITextDrawable.class);
        Assert.assertEquals(new StyledText("def"), styledText.getText());
    }

    @Test
    public void createStyledText() throws StyleParseException {
        loadScript("text/createStyledText");

        LuaTestUtil.assertGlobal("fromString",
                new StyledText("abc"));
        LuaTestUtil.assertGlobal("fromStyledText",
                new StyledText("abc", TextStyle.fromString("color=AABBCCDD")));
    }

    @Test
    public void extendStyle() throws StyleParseException {
        loadScript("text/extendStyle");

        LuaTestUtil.assertGlobal("merged", TextStyle.fromString("fontName=b|color=AABBCCDD"));

    }

    @Test
    public void parseText() {
        loadScript("text/parseText");

        StyledText oneText = LuaTestUtil.getGlobal("oneText", StyledText.class);
        LuaTable oneTriggers = LuaTestUtil.getGlobal("oneTriggers").opttable(null);
        Assert.assertEquals("abc ghi", oneText.toString());
        assertTrigger(oneTriggers, 4, "def");
    }

    @Test
    public void registerBasicTagHandler() {
        loadScript("text/basicTagHandlers");

        LuaValue handlers = LuaTestUtil.getGlobal("handlers");

        // Check that all tags were registered
        for (String tag : BasicTagHandler.getSupportedTags()) {
            LuaValue func = handlers.get(tag);
            Assert.assertTrue(func instanceof BasicTagHandler);
        }
    }

    private void assertTrigger(LuaTable triggers, int charIndex, String functionName) {
        LuaValue val = triggers.get(charIndex);
        Assert.assertTrue(val.isclosure());
        LuaClosure func = val.checkclosure();
        func.call();

        LuaTestUtil.assertGlobal(functionName + "_called", true);
    }

}
