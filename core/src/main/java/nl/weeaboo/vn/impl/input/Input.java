package nl.weeaboo.vn.impl.input;

import java.util.Collection;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.KeyCombination;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

/**
 * Default implementation of {@link IInput}.
 */
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
        for (KeyCombination keyCombination : inputConfig.get(vkey)) {
            if (consumePress(keyCombination)) {
                return true;
            }
        }
        return false;
    }

    private boolean consumePress(KeyCombination keyCombination) {
        if (isJustPressed(keyCombination)) {
            for (KeyCode keyCode : keyCombination.getKeys()) {
                delegate.consumePress(keyCode);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isJustPressed(VKey vkey) {
        for (KeyCombination keyCombination : inputConfig.get(vkey)) {
            if (isJustPressed(keyCombination)) {
                return true;
            }
        }
        return false;
    }

    private boolean isJustPressed(KeyCombination keyCombination) {
        Collection<KeyCode> keys = keyCombination.getKeys();
        if (keys.isEmpty()) {
            return false;
        }

        for (KeyCode keyCode : keys) {
            if (!delegate.isJustPressed(keyCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPressed(VKey vkey, boolean allowConsumedPress) {
        for (KeyCombination keyCombination : inputConfig.get(vkey)) {
            if (isPressed(keyCombination, allowConsumedPress)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPressed(KeyCombination keyCombination, boolean allowConsumedPress) {
        Collection<KeyCode> keys = keyCombination.getKeys();
        if (keys.isEmpty()) {
            return false;
        }

        for (KeyCode keyCode : keys) {
            if (!delegate.isPressed(keyCode, allowConsumedPress)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getPressedTime(VKey vkey, boolean allowConsumedPress) {
        long pressedTime = 0L;
        for (KeyCombination keyCombination : inputConfig.get(vkey)) {
            pressedTime = Math.max(pressedTime, getPressedTime(keyCombination, allowConsumedPress));
        }
        return pressedTime;
    }

    private long getPressedTime(KeyCombination keyCombination, boolean allowConsumedPress) {
        long pressedTime = Long.MAX_VALUE;
        for (KeyCode keyCode : keyCombination.getKeys()) {
            pressedTime = Math.min(pressedTime, delegate.getPressedTime(keyCode, allowConsumedPress));
        }

        if (pressedTime == Long.MAX_VALUE) {
            return 0L;
        }
        return pressedTime;
    }

    @Override
    public Vec2 getPointerPos(Matrix transform) {
        return delegate.getPointerPos(transform);
    }

    @SuppressWarnings("deprecation") // Delegates to deprecated method
    @Override
    public int getPointerScroll() {
        return delegate.getPointerScroll();
    }

    @Override
    public Vec2 getPointerScrollXY() {
        return delegate.getPointerScrollXY();
    }

    @Override
    public boolean isIdle() {
        return delegate.isIdle();
    }

}
