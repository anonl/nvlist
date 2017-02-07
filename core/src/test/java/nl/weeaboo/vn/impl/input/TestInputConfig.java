package nl.weeaboo.vn.impl.input;

import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.VKey;

public final class TestInputConfig {

    // Map every VKey to a unique KeyCode
    public static final KeyCode UP = KeyCode.UP;
    public static final KeyCode DOWN = KeyCode.DOWN;
    public static final KeyCode LEFT = KeyCode.LEFT;
    public static final KeyCode RIGHT = KeyCode.RIGHT;
    public static final KeyCode CONFIRM = KeyCode.BUTTON_C;
    public static final KeyCode CANCEL = KeyCode.ESCAPE;
    public static final KeyCode TEXT_CONTINUE = KeyCode.ENTER;
    public static final KeyCode SKIP = KeyCode.CONTROL_LEFT;
    public static final KeyCode MOUSE_LEFT = KeyCode.MOUSE_LEFT;

    private TestInputConfig() {
    }

    /** Returns a default input config to use in unit tests. */
    public static InputConfig getInstance() {
        InputConfig inputConfig = new InputConfig();
        inputConfig.add(VKey.UP, UP);
        inputConfig.add(VKey.DOWN, DOWN);
        inputConfig.add(VKey.LEFT, LEFT);
        inputConfig.add(VKey.RIGHT, RIGHT);
        inputConfig.add(VKey.CONFIRM, CONFIRM);
        inputConfig.add(VKey.CANCEL, CANCEL);
        inputConfig.add(VKey.TEXT_CONTINUE, TEXT_CONTINUE);
        inputConfig.add(VKey.SKIP, SKIP);
        inputConfig.add(VKey.MOUSE_LEFT, MOUSE_LEFT);
        return inputConfig;
    }

}
