package nl.weeaboo.vn.impl.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.impl.input.MockInput;
import nl.weeaboo.vn.input.VKey;

public final class SkipStateTest {

    private final MockInput input = new MockInput();

    private SkipState skipState;

    @Before
    public void before() {
        skipState = new SkipState();
    }

    /**
     * Control the skip state using the keyboard/mouse.
     */
    @Test
    public void testInputHandling() {
        assertSkipMode(SkipMode.NONE);
        assertSkipUnread(false);

        // Regular skip
        input.buttonPressed(VKey.SKIP);
        skipState.handleInput(input);
        assertSkipMode(SkipMode.PARAGRAPH);
        assertSkipUnread(false);

        // Keeps skipping as long as the button remains pressed
        skipState.handleInput(input);
        assertSkipMode(SkipMode.PARAGRAPH);
        assertSkipUnread(false);

        // Stop skipping when the button is released
        input.buttonReleased(VKey.SKIP);
        skipState.handleInput(input);
        assertSkipMode(SkipMode.NONE);
        assertSkipUnread(false);

        // Alt-skip (also skips unread text)
        input.buttonPressed(VKey.ALT_SKIP);
        skipState.handleInput(input);
        assertSkipMode(SkipMode.PARAGRAPH);
        assertSkipUnread(true);

        // Keeps skipping as long as the button remains pressed
        skipState.handleInput(input);
        assertSkipMode(SkipMode.PARAGRAPH);
        assertSkipUnread(true);
    }

    /**
     * Start/stop skipping.
     */
    @Test
    public void testSkipAndStop() {
        // The skip() method can only increase the skip level
        skipState.skip(SkipMode.PARAGRAPH);
        assertSkipMode(SkipMode.PARAGRAPH);

        skipState.skip(SkipMode.SCENE);
        assertSkipMode(SkipMode.SCENE);

        skipState.skip(SkipMode.PARAGRAPH);
        assertSkipMode(SkipMode.SCENE);

        // Stop skipping
        skipState.stopSkipping();
        assertSkipMode(SkipMode.NONE);
    }

    private void assertSkipUnread(boolean expect) {
        Assert.assertEquals(expect, skipState.shouldSkipLine(false));
    }

    private void assertSkipMode(SkipMode expected) {
        Assert.assertEquals(expected, skipState.getSkipMode());
    }

}
