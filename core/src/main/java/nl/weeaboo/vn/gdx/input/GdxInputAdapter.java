package nl.weeaboo.vn.gdx.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.annotations.VisibleForTesting;

import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.impl.input.InputAccumulator;
import nl.weeaboo.vn.impl.input.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.impl.input.InputAccumulator.PressState;
import nl.weeaboo.vn.impl.input.NativeInput;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;

/**
 * Forwards GDX input events to {@link INativeInput}.
 */
public final class GdxInputAdapter implements IUpdateable, InputProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(GdxInputAdapter.class);

    private final Viewport viewport;

    private final InputAccumulator accum = new InputAccumulator();
    private final NativeInput input = new NativeInput();

    public GdxInputAdapter(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Returns the {@link INativeInput} that this input adapter pushes its key events to.
     */
    public INativeInput getInput() {
        return input;
    }

    @Override
    public void update() {
        input.update(timestampMs(), accum);
    }

    @Override
    public boolean keyDown(int keycode) {
        addKeyboardEvent(keycode, PressState.PRESS);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        addKeyboardEvent(keycode, PressState.RELEASE);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        addMousePositionEvent(screenX, screenY);
        addMouseButtonEvent(button, PressState.PRESS);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        addMousePositionEvent(screenX, screenY);
        addMouseButtonEvent(button, PressState.RELEASE);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        addMousePositionEvent(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        addMousePositionEvent(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        addMouseScrollEvent(amountX, amountY);
        return true;
    }

    private long timestampMs() {
        return TimeUtils.nanoTime() / 1000_000L;
    }

    private void addKeyboardEvent(int keycode, PressState press) {
        LOG.trace("Key event: {} {}", keycode, press);

        accum.addEvent(new ButtonEvent(timestampMs(), convertKeyboard(keycode), press));
    }

    private void addMouseButtonEvent(int button, PressState press) {
        LOG.trace("Mouse button event: {} {}", button, press);

        accum.addEvent(new ButtonEvent(timestampMs(), convertMouse(button), press));
    }

    private void addMousePositionEvent(int screenX, int screenY) {
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));

        LOG.trace("Mouse position event: {}", worldCoords);
        accum.addEvent(new PointerPositionEvent(timestampMs(), worldCoords.x, worldCoords.y));
    }

    private void addMouseScrollEvent(float amountX, float amountY) {
        LOG.trace("Mouse scroll event: {}/{}", amountX, amountY);
        accum.addEvent(new PointerScrollEvent(timestampMs(), amountX, amountY));
    }

    @VisibleForTesting
    static KeyCode convertKeyboard(int keycode) {
        switch (keycode) {
        case Keys.NUM_0: return KeyCode.NUM_0;
        case Keys.NUM_1: return KeyCode.NUM_1;
        case Keys.NUM_2: return KeyCode.NUM_2;
        case Keys.NUM_3: return KeyCode.NUM_3;
        case Keys.NUM_4: return KeyCode.NUM_4;
        case Keys.NUM_5: return KeyCode.NUM_5;
        case Keys.NUM_6: return KeyCode.NUM_6;
        case Keys.NUM_7: return KeyCode.NUM_7;
        case Keys.NUM_8: return KeyCode.NUM_8;
        case Keys.NUM_9: return KeyCode.NUM_9;
        case Keys.A: return KeyCode.A;
        case Keys.ALT_LEFT: return KeyCode.ALT_LEFT;
        case Keys.ALT_RIGHT: return KeyCode.ALT_RIGHT;
        case Keys.APOSTROPHE: return KeyCode.APOSTROPHE;
        case Keys.AT: return KeyCode.AT;
        case Keys.B: return KeyCode.B;
        case Keys.BACK: return KeyCode.BACK;
        case Keys.BACKSLASH: return KeyCode.BACKSLASH;
        case Keys.C: return KeyCode.C;
        case Keys.CALL: return KeyCode.CALL;
        case Keys.CAMERA: return KeyCode.CAMERA;
        case Keys.CAPS_LOCK: return KeyCode.CAPS_LOCK;
        case Keys.CLEAR: return KeyCode.CLEAR;
        case Keys.COMMA: return KeyCode.COMMA;
        case Keys.D: return KeyCode.D;
        case Keys.DEL: return KeyCode.DEL;
        case Keys.FORWARD_DEL: return KeyCode.FORWARD_DEL;
        case Keys.CENTER: return KeyCode.CENTER;
        case Keys.DOWN: return KeyCode.DOWN;
        case Keys.LEFT: return KeyCode.LEFT;
        case Keys.RIGHT: return KeyCode.RIGHT;
        case Keys.UP: return KeyCode.UP;
        case Keys.E: return KeyCode.E;
        case Keys.ENDCALL: return KeyCode.ENDCALL;
        case Keys.ENTER: return KeyCode.ENTER;
        case Keys.ENVELOPE: return KeyCode.ENVELOPE;
        case Keys.EQUALS: return KeyCode.EQUALS;
        case Keys.EXPLORER: return KeyCode.EXPLORER;
        case Keys.F: return KeyCode.F;
        case Keys.FOCUS: return KeyCode.FOCUS;
        case Keys.G: return KeyCode.G;
        case Keys.GRAVE: return KeyCode.GRAVE;
        case Keys.H: return KeyCode.H;
        case Keys.HEADSETHOOK: return KeyCode.HEADSETHOOK;
        case Keys.HOME: return KeyCode.HOME;
        case Keys.I: return KeyCode.I;
        case Keys.J: return KeyCode.J;
        case Keys.K: return KeyCode.K;
        case Keys.L: return KeyCode.L;
        case Keys.LEFT_BRACKET: return KeyCode.LEFT_BRACKET;
        case Keys.M: return KeyCode.M;
        case Keys.MEDIA_FAST_FORWARD: return KeyCode.MEDIA_FAST_FORWARD;
        case Keys.MEDIA_NEXT: return KeyCode.MEDIA_NEXT;
        case Keys.MEDIA_PLAY_PAUSE: return KeyCode.MEDIA_PLAY_PAUSE;
        case Keys.MEDIA_PREVIOUS: return KeyCode.MEDIA_PREVIOUS;
        case Keys.MEDIA_REWIND: return KeyCode.MEDIA_REWIND;
        case Keys.MEDIA_STOP: return KeyCode.MEDIA_STOP;
        case Keys.MENU: return KeyCode.MENU;
        case Keys.MINUS: return KeyCode.MINUS;
        case Keys.MUTE: return KeyCode.MUTE;
        case Keys.N: return KeyCode.N;
        case Keys.NOTIFICATION: return KeyCode.NOTIFICATION;
        case Keys.NUM: return KeyCode.NUM;
        case Keys.NUM_LOCK: return KeyCode.NUM_LOCK;
        case Keys.O: return KeyCode.O;
        case Keys.P: return KeyCode.P;
        case Keys.PAUSE: return KeyCode.PAUSE;
        case Keys.PERIOD: return KeyCode.PERIOD;
        case Keys.PLUS: return KeyCode.PLUS;
        case Keys.POUND: return KeyCode.POUND;
        case Keys.POWER: return KeyCode.POWER;
        case Keys.PRINT_SCREEN: return KeyCode.PRINT_SCREEN;
        case Keys.Q: return KeyCode.Q;
        case Keys.R: return KeyCode.R;
        case Keys.RIGHT_BRACKET: return KeyCode.RIGHT_BRACKET;
        case Keys.S: return KeyCode.S;
        case Keys.SEARCH: return KeyCode.SEARCH;
        case Keys.SCROLL_LOCK: return KeyCode.SCROLL_LOCK;
        case Keys.SEMICOLON: return KeyCode.SEMICOLON;
        case Keys.SHIFT_LEFT: return KeyCode.SHIFT_LEFT;
        case Keys.SHIFT_RIGHT: return KeyCode.SHIFT_RIGHT;
        case Keys.SLASH: return KeyCode.SLASH;
        case Keys.SOFT_LEFT: return KeyCode.SOFT_LEFT;
        case Keys.SOFT_RIGHT: return KeyCode.SOFT_RIGHT;
        case Keys.SPACE: return KeyCode.SPACE;
        case Keys.STAR: return KeyCode.STAR;
        case Keys.SYM: return KeyCode.SYM;
        case Keys.T: return KeyCode.T;
        case Keys.TAB: return KeyCode.TAB;
        case Keys.U: return KeyCode.U;
        case Keys.UNKNOWN: return KeyCode.UNKNOWN;
        case Keys.V: return KeyCode.V;
        case Keys.VOLUME_DOWN: return KeyCode.VOLUME_DOWN;
        case Keys.VOLUME_UP: return KeyCode.VOLUME_UP;
        case Keys.W: return KeyCode.W;
        case Keys.X: return KeyCode.X;
        case Keys.Y: return KeyCode.Y;
        case Keys.Z: return KeyCode.Z;
        case Keys.CONTROL_LEFT: return KeyCode.CONTROL_LEFT;
        case Keys.CONTROL_RIGHT: return KeyCode.CONTROL_RIGHT;
        case Keys.ESCAPE: return KeyCode.ESCAPE;
        case Keys.END: return KeyCode.END;
        case Keys.INSERT: return KeyCode.INSERT;
        case Keys.PAGE_UP: return KeyCode.PAGE_UP;
        case Keys.PAGE_DOWN: return KeyCode.PAGE_DOWN;
        case Keys.PICTSYMBOLS: return KeyCode.PICTSYMBOLS;
        case Keys.SWITCH_CHARSET: return KeyCode.SWITCH_CHARSET;
        case Keys.BUTTON_A: return KeyCode.BUTTON_A;
        case Keys.BUTTON_B: return KeyCode.BUTTON_B;
        case Keys.BUTTON_C: return KeyCode.BUTTON_C;
        case Keys.BUTTON_X: return KeyCode.BUTTON_X;
        case Keys.BUTTON_Y: return KeyCode.BUTTON_Y;
        case Keys.BUTTON_Z: return KeyCode.BUTTON_Z;
        case Keys.BUTTON_L1: return KeyCode.BUTTON_L1;
        case Keys.BUTTON_R1: return KeyCode.BUTTON_R1;
        case Keys.BUTTON_L2: return KeyCode.BUTTON_L2;
        case Keys.BUTTON_R2: return KeyCode.BUTTON_R2;
        case Keys.BUTTON_THUMBL: return KeyCode.BUTTON_THUMBL;
        case Keys.BUTTON_THUMBR: return KeyCode.BUTTON_THUMBR;
        case Keys.BUTTON_START: return KeyCode.BUTTON_START;
        case Keys.BUTTON_SELECT: return KeyCode.BUTTON_SELECT;
        case Keys.BUTTON_MODE: return KeyCode.BUTTON_MODE;
        case Keys.NUMPAD_0: return KeyCode.NUMPAD_0;
        case Keys.NUMPAD_1: return KeyCode.NUMPAD_1;
        case Keys.NUMPAD_2: return KeyCode.NUMPAD_2;
        case Keys.NUMPAD_3: return KeyCode.NUMPAD_3;
        case Keys.NUMPAD_4: return KeyCode.NUMPAD_4;
        case Keys.NUMPAD_5: return KeyCode.NUMPAD_5;
        case Keys.NUMPAD_6: return KeyCode.NUMPAD_6;
        case Keys.NUMPAD_7: return KeyCode.NUMPAD_7;
        case Keys.NUMPAD_8: return KeyCode.NUMPAD_8;
        case Keys.NUMPAD_9: return KeyCode.NUMPAD_9;
        case Keys.NUMPAD_ADD: return KeyCode.NUMPAD_ADD;
        case Keys.NUMPAD_COMMA: return KeyCode.NUMPAD_COMMA;
        case Keys.NUMPAD_DIVIDE: return KeyCode.NUMPAD_DIVIDE;
        case Keys.NUMPAD_DOT: return KeyCode.NUMPAD_DOT;
        case Keys.NUMPAD_ENTER: return KeyCode.NUMPAD_ENTER;
        case Keys.NUMPAD_EQUALS: return KeyCode.NUMPAD_EQUALS;
        case Keys.NUMPAD_LEFT_PAREN: return KeyCode.NUMPAD_LEFT_PAREN;
        case Keys.NUMPAD_RIGHT_PAREN: return KeyCode.NUMPAD_RIGHT_PAREN;
        case Keys.NUMPAD_MULTIPLY: return KeyCode.NUMPAD_MULTIPLY;
        case Keys.NUMPAD_SUBTRACT: return KeyCode.NUMPAD_SUBTRACT;
        case Keys.COLON: return KeyCode.COLON;
        case Keys.F1: return KeyCode.F1;
        case Keys.F2: return KeyCode.F2;
        case Keys.F3: return KeyCode.F3;
        case Keys.F4: return KeyCode.F4;
        case Keys.F5: return KeyCode.F5;
        case Keys.F6: return KeyCode.F6;
        case Keys.F7: return KeyCode.F7;
        case Keys.F8: return KeyCode.F8;
        case Keys.F9: return KeyCode.F9;
        case Keys.F10: return KeyCode.F10;
        case Keys.F11: return KeyCode.F11;
        case Keys.F12: return KeyCode.F12;
        case Keys.F13: return KeyCode.F13;
        case Keys.F14: return KeyCode.F14;
        case Keys.F15: return KeyCode.F15;
        case Keys.F16: return KeyCode.F16;
        case Keys.F17: return KeyCode.F17;
        case Keys.F18: return KeyCode.F18;
        case Keys.F19: return KeyCode.F19;
        case Keys.F20: return KeyCode.F20;
        case Keys.F21: return KeyCode.F21;
        case Keys.F22: return KeyCode.F22;
        case Keys.F23: return KeyCode.F23;
        case Keys.F24: return KeyCode.F24;
        default:
            LOG.warn("Unmapped keyboard button: {}", keycode);
            return KeyCode.UNKNOWN;
        }
    }

    @VisibleForTesting
    static KeyCode convertMouse(int button) {
        switch (button) {
        case Buttons.LEFT: return KeyCode.MOUSE_LEFT;
        case Buttons.MIDDLE: return KeyCode.MOUSE_MIDDLE;
        case Buttons.RIGHT: return KeyCode.MOUSE_RIGHT;
        case Buttons.BACK: return KeyCode.MOUSE_BACK;
        case Buttons.FORWARD: return KeyCode.MOUSE_FORWARD;
        default:
            LOG.warn("Unmapped mouse button: {}", button);
            return KeyCode.UNKNOWN;
        }
    }

}
