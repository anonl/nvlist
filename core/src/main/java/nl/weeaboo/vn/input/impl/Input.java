package nl.weeaboo.vn.input.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

public final class Input implements IInput {

    private final INativeInput delegate;
    private final InputConfig inputConfig;

    public Input(INativeInput delegate, InputConfig inputConfig) {
        this.delegate = Checks.checkNotNull(delegate);
        this.inputConfig = Checks.checkNotNull(inputConfig);
    }

    @Override
    public void clearButtonStates() {
        delegate.clearButtonStates();
    }

    @Override
    public boolean consumePress(VKey vkey) {
        for (KeyCode button : inputConfig.get(vkey)) {
            if (delegate.consumePress(button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isJustPressed(VKey vkey) {
        for (KeyCode button : inputConfig.get(vkey)) {
            if (delegate.isJustPressed(button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPressed(VKey vkey, boolean allowConsumedPress) {
        for (KeyCode button : inputConfig.get(vkey)) {
            if (delegate.isPressed(button, allowConsumedPress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getPressedTime(VKey vkey, boolean allowConsumedPress) {
        long pressedTime = 0L;
        for (KeyCode button : inputConfig.get(vkey)) {
            pressedTime = Math.max(pressedTime, delegate.getPressedTime(button, allowConsumedPress));
        }
        return pressedTime;
    }

    @Override
    public Vec2 getPointerPos(Matrix transform) {
        return delegate.getPointerPos(transform);
    }

    @Override
    public int getPointerScroll() {
        return delegate.getPointerScroll();
    }

    @Override
    public boolean isIdle() {
        return delegate.isIdle();
    }

}
