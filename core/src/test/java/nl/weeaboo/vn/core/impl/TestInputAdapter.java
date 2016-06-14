package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.impl.InputAccumulator;
import nl.weeaboo.vn.input.impl.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.input.impl.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.input.impl.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.input.impl.InputAccumulator.PressState;
import nl.weeaboo.vn.input.impl.NativeInput;
import nl.weeaboo.vn.scene.IVisualElement;

public class TestInputAdapter {

    private final NativeInput input;
    private final InputAccumulator accum = new InputAccumulator();

    private long timestampMs;

    public TestInputAdapter(NativeInput input) {
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

    public void click(IVisualElement elem) {
        mouseFocus(elem);
        mousePress();
        mouseRelease();
    }

    public void mousePress() {
        buttonPressed(KeyCode.MOUSE_LEFT);
    }

    public void mouseRelease() {
        buttonReleased(KeyCode.MOUSE_LEFT);
    }

    public void mouseFocus(IVisualElement elem) {
        Rect2D bounds = elem.getVisualBounds();
        pointerMoved(bounds.x + bounds.w / 2, bounds.y + bounds.h / 2);
    }

}
