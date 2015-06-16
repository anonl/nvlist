package nl.weeaboo.common;

import java.io.Serializable;

/**
 * Holds a non-negative integral width/height pair.
 */
public final class Dim implements Serializable {

	private static final long serialVersionUID = 1L;

	public final int w, h;

	public Dim(int w, int h) {
		if (w < 0 || h < 0) {
			throw new IllegalArgumentException("Dimensions must be >= 0, w=" + w + ", h=" + h);
		}

		this.w = w;
		this.h = h;
	}

	@Override
	public int hashCode() {
		return (w << 16) ^ h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dim) {
			Dim dim = (Dim) obj;
			return w == dim.w && h == dim.h;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Dim(" + w + "x" + h + ")";
	}

}
