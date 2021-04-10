package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.scene.IScreenTextState;

public class LuaTextTest extends LuaIntegrationTest {

    @Test
    public void testStringifiers() {
        loadScript("integration/text/stringifiers");

        LuaTestUtil.assertGlobal("resultApple", new StyledText("banana"));
        LuaTestUtil.assertGlobal("resultCherry", new StyledText("durian"));
        LuaTestUtil.assertGlobal("resultInt", new StyledText("123"));
        LuaTestUtil.assertGlobal("resultFloat", new StyledText("2.5"));
        LuaTestUtil.assertGlobal("resultFuncnil", null);
        LuaTestUtil.assertGlobal("resultFuncnone", null);
    }

    @Test
    public void testTagHandlers() {
        loadScript("integration/text/taghandlers");

        IScreenTextState textState = mainContext.getScreen().getTextState();

        MutableStyledText expected = new MutableStyledText("123");
        expected.setStyle(TextStyle.BOLD, 1, 2);
        Assert.assertEquals(expected.immutableCopy(), textState.getText());
    }

    @Test
    public void testSpeakerRegistry() {
        loadScript("integration/text/speakers");

        IScreenTextState textState = mainContext.getScreen().getTextState();

        MutableStyledText expected = new MutableStyledText("test");
        expected.setStyle(TextStyle.ITALIC);
        Assert.assertEquals(expected.immutableCopy(), textState.getText());

        textContinue();
        Assert.assertEquals(new StyledText("line2"), textState.getText());

        textContinue();
        Assert.assertEquals(new StyledText("line3"), textState.getText());
    }

    @Test
    public void testTextlog() {
        loadScript("integration/text/textlog");

        LuaTestUtil.assertGlobal("finished", false);

        // Show textlog screen
        buttonPress(VKey.SHOW_TEXT_LOG);

        // Close textlog screen
        buttonPress(VKey.DOWN);

        // Cancel the waitClick() and let the script finish
        textContinue();
        waitForAllThreads();

        LuaTestUtil.assertGlobal("finished", true);
    }

}
