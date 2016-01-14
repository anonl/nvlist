package nl.weeaboo.vn.scene.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IInput;

public final class InputSignal extends AbstractSignal {

    public final IInput input;

    public InputSignal(IInput input) {
        this.input = Checks.checkNotNull(input);
    }

}
