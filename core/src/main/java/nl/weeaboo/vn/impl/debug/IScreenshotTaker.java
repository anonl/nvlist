package nl.weeaboo.vn.impl.debug;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.input.INativeInput;

interface IScreenshotTaker {

    /** Handle input and update internal state. */
    void update(IEnvironment env, INativeInput input);

}
