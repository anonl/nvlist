package nl.weeaboo.vn.impl.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

public class InputTest {

    private NativeInput input;
    private TestInputAdapter inputAdapter;

    @Before
    public void before() {
        input = new NativeInput();
        inputAdapter = new TestInputAdapter(input);
    }

    /** Basic keyboard/mouse button support */
    @Test
    public void buttonSupport() {
        KeyCode key = KeyCode.X;

        // Initial state is not pressed
        assertButton(key, false, false);

        // Generate a button press
        inputAdapter.buttonPressed(key);
        inputAdapter.updateInput();
        assertButton(key, true, true);

        // Consume button press, this clears the justPressed state
        Assert.assertTrue(input.consumePress(key));
        Assert.assertFalse(input.isJustPressed(key));
        Assert.assertTrue(input.isPressed(key, true));
        Assert.assertFalse(input.isPressed(key, false));

        // Generate another button press
        inputAdapter.buttonPressed(key);
        inputAdapter.updateInput();
        assertButton(key, true, true);

        // The next frame, the justPressed state is cleared
        inputAdapter.updateInput();
        assertButton(key, true, false);

        // After a button release event, the pressed state is cleared
        inputAdapter.buttonReleased(key);
        inputAdapter.updateInput();
        assertButton(key, false, false);
    }

    /** Check behavior of the various button state getters */
    @Test
    public void defaultButtonStates() {
        KeyCode key = KeyCode.Y;
        Assert.assertFalse(input.consumePress(key));
        Assert.assertFalse(input.isJustPressed(key));
        Assert.assertFalse(input.isPressed(key, false));
        Assert.assertFalse(input.isPressed(key, true));
        Assert.assertEquals(0, input.getPressedTime(key, true));
    }

    private void assertButton(KeyCode key, boolean pressed, boolean justPressed) {
        Assert.assertEquals(pressed, input.isPressed(key, true));
        Assert.assertEquals(justPressed, input.isJustPressed(key));
    }

    /** Mouse position support */
    @Test
    public void pointerPosition() {
        // Initial mouse position is (0, 0)
        assertPointerPos(0, 0);

        // Single mouse move event in a frame
        inputAdapter.pointerMoved(12, 34);
        inputAdapter.updateInput();
        assertPointerPos(12, 34);

        // Multiple mouse move events in a single frame
        inputAdapter.pointerMoved(23, 45);
        inputAdapter.pointerMoved(34, 56);
        inputAdapter.updateInput();
        assertPointerPos(34, 56); // Second pos overwrites the first one

        // No new mouse move events received
        inputAdapter.updateInput();
        assertPointerPos(34, 56); // Same pos as last time
    }

    /** Mouse scroll wheel support */
    @Test
    public void pointerScroll() {
        // Initial scroll value is 0
        assertPointerScroll(0);

        // Single scroll event
        inputAdapter.pointerScrolled(7);
        inputAdapter.updateInput();
        assertPointerScroll(7);

        // Multiple scroll events are summed
        inputAdapter.pointerScrolled(-5);
        inputAdapter.pointerScrolled(3);
        inputAdapter.updateInput();
        assertPointerScroll(-5 + 3);

        // Scroll amount is cleared the next frame
        inputAdapter.updateInput();
        assertPointerScroll(0);
    }

    private void assertPointerPos(int x, int y) {
        // Assert using whole-pixel coordinates, no level of sub-pixel accuracy is guaranteed
        Vec2 pointerPos = input.getPointerPos(Matrix.identityMatrix());
        Assert.assertEquals(x, Math.round(pointerPos.x));
        Assert.assertEquals(y, Math.round(pointerPos.y));
    }

    private void assertPointerScroll(int expected) {
        Assert.assertEquals(expected, input.getPointerScroll());
    }

}
