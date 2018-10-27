package nl.weeaboo.vn.impl.scene;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.input.MockInput;
import nl.weeaboo.vn.impl.script.ScriptEventDispatcher;
import nl.weeaboo.vn.impl.script.ScriptFunctionStub;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.impl.text.TestFontStore;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.ScriptException;

public class ButtonClickTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private MockInput input;

    private ScriptEventDispatcher eventDispatcher;
    private ScriptFunctionStub clickFunction;
    private Button button;

    @Before
    public void before() {
        input = new MockInput();

        eventDispatcher = new ScriptEventDispatcher();
        clickFunction = new ScriptFunctionStub();
        TestFontStore fontStore = new TestFontStore();
        button = new Button(eventDispatcher, fontStore);
        button.setClickHandler(clickFunction);
        button.setSize(100, 20);
    }

    /** Test basic behavior */
    @Test
    public void clickHappyFlow() {
        // Mouse press on top of the button
        pressButton();
        assertButtonState(true, true, 0);

        // Mouse release on top of the button
        releaseButton();
        assertButtonState(true, false, 1);
    }

    /** Test basic behavior for toggle buttons */
    @Test
    public void toggleButtonHappyFlow() {
        Assert.assertFalse(button.isToggle());
        button.setToggle(true);
        Assert.assertTrue(button.isToggle());

        // Set toggled
        button.setSelected(true);

        pressButton();
        assertButtonState(true, true, 0);
        assertSelected(true);

        releaseButton();
        assertButtonState(true, false, 1);
        assertSelected(false); // Button is no longer toggled
    }

    /** Behavior when clicking a disabled button */
    @Test
    public void clickDisabledButton() {
        button.setEnabled(false);

        // Nothing happens if the button is disabled
        pressButton();
        assertButtonState(false, false, 0);
        releaseButton();
        assertButtonState(false, false, 0);
    }

    /** Behavior when no click handler is registered */
    @Test
    public void noClickHandler() {
        Assert.assertEquals(clickFunction, button.getClickHandler());
        button.setClickHandler(null);

        pressButton();
        assertButtonState(true, true, 0);
        Assert.assertEquals(false, button.consumePress());

        releaseButton();
        assertButtonState(true, false, 0); // ClickHandler not called
        Assert.assertEquals(true, button.consumePress()); // Click can be consumed manually
    }

    /** Test functionality of {@link IButton#setTouchMargin} */
    @Test
    public void touchMargin() {
        Assert.assertEquals(0, button.getTouchMargin(), EPSILON);
        input.pointerMoved(-5, 0);
        input.mousePress();
        handleInput();
        assertButtonState(false, false, 0);

        // Increase touch margin so the mouse pointer now fall inside the collision shape
        button.setTouchMargin(10);
        input.mousePress();
        handleInput();
        assertButtonState(true, true, 0);

        // Reduce the touch margin again so the pointer is outside the collision shape
        button.setTouchMargin(0);
        handleInput();
        assertButtonState(false, false, 0);
    }

    private void pressButton() {
        input.mouseFocus(button);
        input.mousePress();
        handleInput();
    }

    private void releaseButton() {
        input.mouseRelease();
        handleInput();
    }

    private void assertButtonState(boolean rollover, boolean pressed, int clickCount) {
        Assert.assertEquals(rollover, button.isRollover());
        Assert.assertEquals(pressed, button.isPressed());
        Assert.assertEquals(clickCount, clickFunction.consumeCallCount());
    }

    private void assertSelected(boolean expected) {
        Assert.assertEquals(expected, button.isSelected());
    }

    private void handleInput() {
        button.handleInput(Matrix.identityMatrix(), input);

        for (IScriptFunction func : eventDispatcher.retrieveWork()) {
            try {
                func.call();
            } catch (ScriptException e) {
                Assert.fail(e.toString());
            }
        }
    }

}
