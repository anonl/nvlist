package nl.weeaboo.vn.impl.input;

import nl.weeaboo.vn.impl.input.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PressState;
import nl.weeaboo.vn.input.KeyCode;

/**
 * @see InputMock
 */
final class TestInputAdapter {

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
    public void pointerScrolled(float amountX, float amountY) {
        accum.addEvent(new PointerScrollEvent(getTime(), amountX, amountY));
    }

    /** Adds a button press event to the input buffer. */
    public void buttonPressed(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.PRESS));
    }

    /** Adds a button release event to the input buffer. */
    public void buttonReleased(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.RELEASE));
    }

    /** Adds a left mouse button press event to the input buffer. */
    public void mousePress() {
        buttonPressed(KeyCode.MOUSE_LEFT);
    }

    /** Adds a left mouse button release event to the input buffer. */
    public void mouseRelease() {
        buttonReleased(KeyCode.MOUSE_LEFT);
    }

}
