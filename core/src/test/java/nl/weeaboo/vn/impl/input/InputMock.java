package nl.weeaboo.vn.impl.input;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.scene.IVisualElement;

public class InputMock implements IInput, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<VKey, ButtonState> buttonStates = new HashMap<>();
    private final Vec2 pointerPos = new Vec2();
    private int pointerScroll;

    private long timestampMs;

    @Override
    public void clearButtonStates() {
        buttonStates.clear();
    }

    @Override
    public boolean consumePress(VKey button) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.consumePress();
    }

    @Override
    public boolean isJustPressed(VKey button) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.isJustPressed();
    }

    @Override
    public boolean isPressed(VKey button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.isPressed(allowConsumedPress);
    }

    @Override
    public long getPressedTime(VKey button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return 0;
        }
        return state.getPressedTime(timestampMs, allowConsumedPress);
    }

    @Override
    public Vec2 getPointerPos(Matrix transform) {
        Vec2 result = new Vec2(pointerPos);
        transform.transform(result);
        return result;
    }

    @Override
    public int getPointerScroll() {
        return pointerScroll;
    }

    @Override
    public boolean isIdle() {
        return false;
    }

    private ButtonState getButtonState(VKey button, boolean createIfNeeded) {
        ButtonState state = buttonStates.get(button);
        if (state == null && createIfNeeded) {
            state = new ButtonState();
            buttonStates.put(button, state);
        }
        return state;
    }


    /** Returns the internal timestamp. */
    public synchronized long getTime() {
        return timestampMs;
    }

    /** Sets the internal timestamp. */
    public synchronized void setTime(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    /** Increments the internal timestamp by the specified amount. */
    public synchronized void increaseTime(int increaseMs) {
        setTime(getTime() + increaseMs);
    }

    /** Adds a pointer position event to the input buffer. */
    public void pointerMoved(double x, double y) {
        pointerPos.x += x;
        pointerPos.y += y;
    }

    /** Adds a pointer scroll event to the input buffer. */
    public void pointerScrolled(int amount) {
        pointerScroll += amount;
    }

    /** Adds a button press event to the input buffer. */
    public void buttonPressed(VKey button) {
        getButtonState(button, true).onPressed(timestampMs);
    }

    /** Adds a button release event to the input buffer. */
    public void buttonReleased(VKey button) {
        getButtonState(button, true).onReleased(timestampMs);
    }

    /**
     * Adds events to the input buffer for the following sequence of events:
     * <ol>
     * <li>Move the pointer over the element.
     * <li>Press the left mouse button.
     * <li>Release the left mouse button.
     * </ol>
     */
    public void click(IVisualElement elem) {
        mouseFocus(elem);
        mousePress();
        mouseRelease();
    }

    /** Adds a left mouse button press event to the input buffer. */
    public void mousePress() {
        buttonPressed(VKey.MOUSE_LEFT);
    }

    /** Adds a left mouse button release event to the input buffer. */
    public void mouseRelease() {
        buttonReleased(VKey.MOUSE_LEFT);
    }

    /** Moves the mouse pointer over the specified element. */
    public void mouseFocus(IVisualElement elem) {
        Rect2D bounds = elem.getVisualBounds();
        pointerMoved(bounds.x + bounds.w / 2, bounds.y + bounds.h / 2);
    }

}
