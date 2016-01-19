package nl.weeaboo.vn.core.impl;

public final class ButtonState {

    private boolean consumed;
    private boolean pressed;
    private long pressedStateSinceMs;

    public boolean consumePress() {
        boolean result = isJustPressed();
        consumed = true;
        return result;
    }

    public boolean isJustPressed() {
        return pressed && !consumed;
    }

    public boolean isPressed(boolean allowConsumedPress) {
        if (consumed && !allowConsumedPress) {
            return false;
        }
        return pressed;
    }

    public long getPressedTime(long timestampMs, boolean allowConsumedPress) {
        if (!pressed) {
            return 0L;
        }
        if (consumed && !allowConsumedPress) {
            return 0L;
        }
        return timestampMs - pressedStateSinceMs;
    }

    public void onPressed(long timestampMs) {
        if (!pressed) {
            consumed = false;
            pressed = true;
            pressedStateSinceMs = timestampMs;
        }
    }

    public void onReleased(long timestampMs) {
        if (pressed) {
            consumed = false;
            pressed = false;
            pressedStateSinceMs = timestampMs;
        }
    }

}
