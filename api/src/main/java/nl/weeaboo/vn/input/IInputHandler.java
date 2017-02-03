package nl.weeaboo.vn.input;

import nl.weeaboo.vn.math.Matrix;

public interface IInputHandler {

    /**
     * Callback for handling user input.
     *
     * @param input Keyboard/mouse/gamepad input
     */
    void handleInput(Matrix parentTransform, IInput input);

}
