package nl.weeaboo.vn.scene.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.input.INativeInput;

public final class InputSignal extends AbstractSignal {

    public final INativeInput input;

    public InputSignal(INativeInput input) {
        this.input = Checks.checkNotNull(input);
    }

}
