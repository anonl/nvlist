package nl.weeaboo.vn.impl.test.integration.lua;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.impl.scene.SceneUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.stats.ISeenLogHolder;

public class LuaChoiceTest extends LuaIntegrationTest {

    @Test
    public void testBasicChoice() {
        loadScript("integration/choice/basic-choice");
        waitForAllThreads();
    }

    /**
     * The styling difference for (un)selected choices must be applied every frame so the styling remains
     * accurate when loading an older save file.
     */
    @Test
    public void testLoadChoice() {
        loadScript("integration/choice/load-choice");
        env.update();

        IButton button = findButton("a");
        assertTextColor(button, 0xFFFFFFFF);

        // Mark choice as read -> text style updates (so the style is accurate when loading an older save)
        ISeenLogHolder seenLog = env.getStatsModule().getSeenLog();
        seenLog.getChoiceLog().markChoiceSelected("integration/choice/load-choice.lvn:6", 1);
        env.update();
        assertTextColor(button, 0xFF808080);

        // Select choice "a"
        button.click();
        waitForAllThreads();

        // choice() return the 1-based index of the selected option
        LuaTestUtil.assertGlobal("selected", 1);
    }

    private IButton findButton(String label) {
        IScreen screen = mainContext.getScreen();
        return SceneUtil.findFirst(screen.getActiveLayer(), IButton.class,
                b -> b.getText().toString().equals(label));
    }

    private void assertTextColor(IButton button, int expectedArgb) {
        StyledText text = button.getText();
        for (int n = 0; n < text.length(); n++) {
            Assert.assertEquals(expectedArgb, text.getStyle(n).getColor());
        }
    }
}
