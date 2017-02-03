package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IInterpolator extends Serializable {

    /**
     * Remaps an input value in the range {@code (0, 1)} to an output value in the range {@code (0, 1)}.
     */
    float remap(float x);

}
