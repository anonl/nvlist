package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.core.KeyCode;
import nl.weeaboo.vn.core.impl.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.core.impl.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.core.impl.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.core.impl.InputAccumulator.PressState;

public class TestInputAdapter {

    private final Input input;
    private final InputAccumulator accum = new InputAccumulator();

    private long timestampMs;

    public TestInputAdapter(Input input) {
        this.input = input;
    }

    public void updateInput(int timeIncrease) {
        increaseTime(timeIncrease);
        updateInput();
    }
    public void updateInput() {
        input.update(getTime(), accum);
    }

    public synchronized long getTime() {
        return timestampMs;
    }

    public synchronized void setTime(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    public synchronized void increaseTime(int increaseMs) {
        setTime(getTime() + increaseMs);
    }

    public void pointerMoved(double x, double y) {
        accum.addEvent(new PointerPositionEvent(getTime(), x, y));
    }

    public void pointerScrolled(int amount) {
        accum.addEvent(new PointerScrollEvent(getTime(), amount));
    }

    public void buttonPressed(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.PRESS));
    }

    public void buttonReleased(KeyCode button) {
        accum.addEvent(new ButtonEvent(getTime(), button, PressState.RELEASE));
    }

}
