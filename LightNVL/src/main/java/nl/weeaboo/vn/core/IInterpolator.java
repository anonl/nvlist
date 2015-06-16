package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IInterpolator extends Serializable {

	/**
	 * Remaps an input value in the range <code>(0, 1)</code> to an output value
	 * in the range <code>(0, 1)</code>
	 */
	public float remap(float x);
	
}
