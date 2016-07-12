package nl.weeaboo.vn.input;

import nl.weeaboo.vn.math.Matrix;

public interface IInputHandler {

    /**
     * @param input Keyboard/mouse/gamepad input
     */
    void handleInput(Matrix parentTransform, IInput input);

}
