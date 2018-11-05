package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.styledtext.StyleParseException;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.text.ILoadingFontStore;

public class TextLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new TextLib(env, scriptEnv));
    }

    @Test
    public void createStyle() throws StyleParseException {
        loadScript("integration/text/createStyle");

        LuaTestUtil.assertGlobal("simple",
                TextStyle.fromString("fontName=a|fontSize=13.24"));
        LuaTestUtil.assertGlobal("complex1",
                TextStyle.fromString("fontName=b|fontStyle=italic|color=AABBCCDD"));
    }

    @Test
    public void createTextDrawable() {
        loadScript("integration/text/createTextDrawable");

        ITextDrawable fullDefault = LuaTestUtil.getGlobal("fullDefault", ITextDrawable.class);
        Assert.assertNotNull(fullDefault);

        ITextDrawable plainText = LuaTestUtil.getGlobal("plainText", ITextDrawable.class);
        Assert.assertEquals(new StyledText("abc"), plainText.getText());

        ITextDrawable styledText = LuaTestUtil.getGlobal("styledText", ITextDrawable.class);
        Assert.assertEquals(new StyledText("def"), styledText.getText());
    }

    @Test
    public void createStyledText() throws StyleParseException {
        loadScript("integration/text/createStyledText");

        LuaTestUtil.assertGlobal("fromString",
                new StyledText("abc"));
        LuaTestUtil.assertGlobal("fromStyledText",
                new StyledText("abc", TextStyle.fromString("color=AABBCCDD")));
    }

    @Test
    public void extendStyle() throws StyleParseException {
        loadScript("integration/text/extendStyle");

        LuaTestUtil.assertGlobal("merged", TextStyle.fromString("fontName=b|color=AABBCCDD"));

    }

    @Test
    public void parseText() {
        loadScript("integration/text/parseText");

        StyledText oneText = LuaTestUtil.getGlobal("oneText", StyledText.class);
        LuaTable oneTriggers = LuaTestUtil.getGlobal("oneTriggers").opttable(null);
        Assert.assertEquals("abc ghi", oneText.toString());
        assertTrigger(oneTriggers, 4, "def");

        StyledText twoText = LuaTestUtil.getGlobal("twoText", StyledText.class);
        LuaTable twoTriggers = LuaTestUtil.getGlobal("twoTriggers").opttable(null);
        Assert.assertEquals("abc ghi", twoText.toString());
        assertTrigger(twoTriggers, 4, "def");
    }

    @Test
    public void registerBasicTagHandler() {
        loadScript("integration/text/basicTagHandlers");

        LuaValue handlers = LuaTestUtil.getGlobal("handlers");

        // Check that all tags were registered
        for (String tag : BasicTagHandler.getSupportedTags()) {
            LuaValue func = handlers.get(tag);
            Assert.assertTrue(func instanceof BasicTagHandler);
        }
    }

    @Test
    public void testSetDefaultStyle() {
        loadScript("integration/text/setDefaultTextStyle");

        ILoadingFontStore fontStore = env.getTextModule().getFontStore();
        Assert.assertEquals("test", fontStore.getDefaultStyle().getFontName());
    }

    @Test
    public void testFormat() {
        loadScript("integration/text/format");

        StyledText result = LuaTestUtil.getGlobal("result", StyledText.class);
        Assert.assertEquals(new StyledText("Test abc 123"), result);

        StyledText formatStyled = LuaTestUtil.getGlobal("formatStyled", StyledText.class);
        Assert.assertEquals(new StyledText("Styled 123"), formatStyled);

        Assert.assertEquals(StyledText.EMPTY_STRING, LuaTestUtil.getGlobal("missingFormat", StyledText.class));

        Assert.assertEquals(LuaString.valueOf("error"), LuaTestUtil.getGlobal("missingArg"));
        Assert.assertEquals(LuaString.valueOf("error"), LuaTestUtil.getGlobal("extraArg"));
    }

    @Test
    public void testParseLuaLiteral() {
        loadScript("integration/text/lualiteral");

        LuaTestUtil.assertGlobal("retNil", null);
        LuaTestUtil.assertGlobal("retTrue", true);
        LuaTestUtil.assertGlobal("retFalse", false);
        LuaTestUtil.assertGlobal("retStringSingle", "string");
        LuaTestUtil.assertGlobal("retStringDouble", "string");
        LuaTestUtil.assertGlobal("retNumber", 12.5);
        LuaTestUtil.assertGlobal("retNumberHex", 0xFEDC4321);
    }

    private void assertTrigger(LuaTable triggers, int charIndex, String functionName) {
        LuaValue val = triggers.get(charIndex);
        Assert.assertTrue(val.isclosure());
        LuaClosure func = val.checkclosure();
        func.call();

        LuaTestUtil.assertGlobal(functionName + "_called", true);
    }

}
