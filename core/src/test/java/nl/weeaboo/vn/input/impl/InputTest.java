package nl.weeaboo.vn.input.impl;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.core.impl.TestInputAdapter;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

public class InputTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private TestInputAdapter inputAdapter;
    private NativeInput nativeInput;
    private Input input;

    @Before
    public void before() throws IOException {
        nativeInput = new NativeInput();
        inputAdapter = new TestInputAdapter(nativeInput);
        input = new Input(nativeInput, InputConfig.readDefaultConfig());
    }

    @Test
    public void consumePress() {
        consumePress(false, VKey.TEXT_CONTINUE);

        // Single key
        press(KeyCode.ENTER);
        consumePress(true, VKey.TEXT_CONTINUE);
        consumePress(false, VKey.TEXT_CONTINUE);

        // Multiple keys (ENTER and MOUSE_LEFT are both mapped to TEXT_CONTINUE)
        press(KeyCode.ENTER, KeyCode.MOUSE_LEFT);
        assertPressed(true, KeyCode.ENTER);
        assertPressed(true, KeyCode.MOUSE_LEFT);

        consumePress(true, VKey.TEXT_CONTINUE); // Consumes ENTER (first defined in mapping)
        assertPressed(false, KeyCode.ENTER);
        assertPressed(true, KeyCode.MOUSE_LEFT);

        consumePress(true, VKey.TEXT_CONTINUE); // Consume MOUSE_LEFT (second defined in mapping)
        assertPressed(false, KeyCode.ENTER);
        assertPressed(false, KeyCode.MOUSE_LEFT);

        consumePress(false, VKey.TEXT_CONTINUE);
    }

    @Test
    public void isPressed() {
        assertPressed(false, VKey.TEXT_CONTINUE);

        // Single key pressed
        press(KeyCode.ENTER);
        assertPressed(true, VKey.TEXT_CONTINUE);
        clearPressed();

        // Other valid key pressed
        press(KeyCode.MOUSE_LEFT);
        assertPressed(true, VKey.TEXT_CONTINUE);
        clearPressed();

        // Both keys pressed
        press(KeyCode.ENTER, KeyCode.MOUSE_LEFT);
        assertPressed(true, VKey.TEXT_CONTINUE);

        // One of the valid keys is consumed
        nativeInput.consumePress(KeyCode.ENTER);
        assertPressed(true, VKey.TEXT_CONTINUE);

        // Both keys consumed
        nativeInput.consumePress(KeyCode.MOUSE_LEFT);
        assertPressed(false, VKey.TEXT_CONTINUE);
    }

    @Test
    public void isJustPressed() {
        assertJustPressed(false, VKey.TEXT_CONTINUE);

        // Single key pressed
        press(KeyCode.ENTER);
        assertJustPressed(true, VKey.TEXT_CONTINUE);
        clearPressed();

        // Other valid key pressed
        press(KeyCode.MOUSE_LEFT);
        assertJustPressed(true, VKey.TEXT_CONTINUE);
        clearPressed();

        // Both keys pressed
        press(KeyCode.ENTER, KeyCode.MOUSE_LEFT);
        assertJustPressed(true, VKey.TEXT_CONTINUE);
    }

    @Test
    public void pressedTime() {
        inputAdapter.buttonPressed(KeyCode.ENTER);
        inputAdapter.updateInput(10);
        assertPressTime(10, VKey.TEXT_CONTINUE);

        inputAdapter.buttonPressed(KeyCode.MOUSE_LEFT);
        inputAdapter.updateInput(10);
        // Press time is the longest press time of all the valid keys
        assertPressTime(20, VKey.TEXT_CONTINUE);

        inputAdapter.buttonReleased(KeyCode.ENTER);
        inputAdapter.updateInput();
        // Press time decreased because now only MOUSE_LEFT is pressed
        assertPressTime(10, VKey.TEXT_CONTINUE);
    }

    /** Pointer-related methods just delegate to the wrapped nativeInput */
    @Test
    public void pointerDelegate() {
        inputAdapter.pointerMoved(1, 2);
        inputAdapter.pointerScrolled(3);

        Vec2 expectedPos = input.getPointerPos(Matrix.identityMatrix());
        Vec2 actualPos = input.getPointerPos(Matrix.identityMatrix());
        Assert.assertEquals(expectedPos.x, actualPos.x, EPSILON);
        Assert.assertEquals(expectedPos.y, actualPos.y, EPSILON);
        Assert.assertEquals(nativeInput.getPointerScroll(), input.getPointerScroll());
    }

    private void clearPressed() {
        input.clearButtonStates();
    }

    private void assertPressed(boolean expected, VKey key) {
        Assert.assertEquals(expected, input.isPressed(key, false));
    }

    private void assertJustPressed(boolean expected, VKey key) {
        Assert.assertEquals(expected, input.isJustPressed(key));
    }
    private void assertPressed(boolean expected, KeyCode button) {
        Assert.assertEquals(expected, nativeInput.isPressed(button, false));
    }

    private void assertPressTime(int expectedTime, VKey vkey) {
        Assert.assertEquals(expectedTime, input.getPressedTime(vkey, false));
    }

    private void consumePress(boolean expected, VKey vkey) {
        Assert.assertEquals(expected, input.consumePress(vkey));
    }

    private void press(KeyCode... pressed) {
        for (KeyCode button : pressed) {
            inputAdapter.buttonPressed(button);
        }
        inputAdapter.updateInput();
    }
}
