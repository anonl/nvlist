package nl.weeaboo.vn.impl.core;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.input.InputAccumulator;
import nl.weeaboo.vn.impl.input.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PressState;
import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.scene.IVisualElement;

public class TestInputAdapter {

    private final NativeInput input;
    private final InputAccumulator accum = new InputAccumulator();

    private long timestampMs;

    public TestInputAdapter(NativeInput input) {
        this.input = input;
    }

    /**
     * Increase the internal timestamp, then calls {@link #updateInput()}.
     *
     * @see #increaseTime(int)
     * @see #updateInput()
     */
    public void updateInput(int timeIncrease) {
        increaseTime(timeIncrease);
        updateInput();
    }

    /**
     * Processes the buffered input events.
     */
    public void updateInput() {
        input.update(getTime(), accum);
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
        accum.addEvent(new PointerPositionEvent(getTime(), x, y));
    }

    /** Adds a pointer scroll event to the input buffer. */
    public void pointerScrolled(int amount) {
        accum.addEvent(new PointerScrollEvent(getTime(), amount));
    }

    /** Adds a button press event to the input buffer. */
    public void buttonPressed(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.PRESS));
    }

    /** Adds a button release event to the input buffer. */
    public void buttonReleased(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.RELEASE));
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
        buttonPressed(KeyCode.MOUSE_LEFT);
    }

    /** Adds a left mouse button release event to the input buffer. */
    public void mouseRelease() {
        buttonReleased(KeyCode.MOUSE_LEFT);
    }

    /** Moves the mouse pointer over the specified element. */
    public void mouseFocus(IVisualElement elem) {
        Rect2D bounds = elem.getVisualBounds();
        pointerMoved(bounds.x + bounds.w / 2, bounds.y + bounds.h / 2);
    }

}
