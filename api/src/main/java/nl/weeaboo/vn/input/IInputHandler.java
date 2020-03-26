package nl.weeaboo.vn.input;

import nl.weeaboo.vn.math.Matrix;

/**
 * Receives input events.
 */
public interface IInputHandler {

    /**
     * Callback for handling user input.
     *
     * @param input Keyboard/mouse/gamepad input
     */
    void handleInput(Matrix parentTransform, IInput input);

}
