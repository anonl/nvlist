package nl.weeaboo.vn.gdx.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

/** Generates keyboard/mouse events */
public class GdxInputRobot {

    private static final Logger LOG = LoggerFactory.getLogger(GdxInputRobot.class);

    private InputProcessor target;

    public GdxInputRobot(InputProcessor target) {
        this.target = target;
    }

    /**
     * Simulates pressing the corresponding keyboard keys to type out the given text. Only works for basic text
     * (lowercase alphanumeric and spaces).
     */
    public GdxInputRobot type(String text) {
        LOG.trace("Type text: \"{}\"", text);

        for (int n = 0; n < text.length(); n++) {
            type(text.charAt(n));
        }

        return this;
    }

    /**
     * Simulates pressing the keyboard button corresponding to {@code keyChar}.
     */
    public GdxInputRobot type(char keyChar) {
        // Try to find the keycode so we can generate the appropriate key down/up events as well
        int keyCode = Keys.valueOf(Character.toString(Character.toUpperCase(keyChar)));
        if (keyCode < 0) {
            LOG.trace("Unable to determine keyCode for char '{}'", keyChar);
        } else {
            doType(keyCode);
        }

        doType(keyChar);

        return this;
    }

    /**
     * Simulates pressing the keyboard button with the given key code.
     */
    public GdxInputRobot type(int keyCode) {
        doType(keyCode);

        // Try to find the matching key char
        String string = Keys.toString(keyCode);
        if (string.length() == 1) {
            doType(Character.toLowerCase(string.charAt(0)));
        }

        return this;
    }

    /**
     * Simulates typing the specified text, then pressing enter.
     * @see #type(String)
     */
    public GdxInputRobot enter(String text) {
        type(text);
        enter();

        return this;
    }

    /** Simulates pressing the enter key */
    public GdxInputRobot enter() {
        doType(Keys.ENTER);
        doType('\n');
        return this;
    }

    private void doType(int keyCode) {
        target.keyDown(keyCode);
        target.keyUp(keyCode);
    }

    private void doType(char keyChar) {
        target.keyTyped(keyChar);
    }

}
