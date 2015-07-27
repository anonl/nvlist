package nl.weeaboo.common;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Like Rect2D, but accepts negative values.
 */
public final class Area2D implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Area2D EMPTY = new Area2D(0, 0, 0, 0);

	public final double x, y, w, h;

	private Area2D(double x, double y, double w, double h) {
		if (Double.isNaN(w) || Double.isInfinite(w) || Double.isNaN(h) || Double.isInfinite(h)) {
			throw new IllegalArgumentException("Dimensions must finite, w=" + w + ", h=" + h);
		}

		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static Area2D of(double x, double y, double w, double h) {
		return new Area2D(x, y, w, h);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new double[] {x, y, w, h});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Area2D) {
			Area2D dim = (Area2D)obj;
			return x == dim.x && y == dim.y && w == dim.w && h == dim.h;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Area2D(" + x + "," + y + "," + w + "," + h + ")";
	}

    public Area2D flipped(boolean horizontal, boolean vertical) {
        double nx = x, ny = y, nw = w, nh = h;
        if (horizontal) {
            nx += w;
            nw = -nw;
        }
        if (vertical) {
            ny += h;
            nh = -nh;
        }
        return Area2D.of(nx, ny, nw, nh);
    }

}
