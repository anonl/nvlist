package nl.weeaboo.vn.impl.input;

final class ButtonState {

    private boolean consumed;
    private boolean pressed;
    private boolean isJustPressed;
    private long pressedStateSinceMs;

    public boolean consumePress() {
        boolean result = isJustPressed();
        consumed = true;
        return result;
    }

    public boolean isJustPressed() {
        return pressed && isJustPressed && !consumed;
    }

    public void clearJustPressed() {
        isJustPressed = false;
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
        consumed = false;
        isJustPressed = true;
        pressed = true;
        pressedStateSinceMs = timestampMs;
    }

    public void onReleased(long timestampMs) {
        consumed = false;
        isJustPressed = false;
        pressed = false;
        pressedStateSinceMs = timestampMs;
    }

}
