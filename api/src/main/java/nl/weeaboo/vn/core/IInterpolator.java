package nl.weeaboo.vn.core;

import java.io.Serializable;

/**
 * Remaps floating point values between {@code 0.0)} and {@code 1.0} (both inclusive). Typically used for
 * graphical effects.
 */
public interface IInterpolator extends Serializable {

    /**
     * Remaps an input value in the range {@code [0, 1]} to an output value in the range {@code [0, 1]}.
     */
    float remap(float x);

}
