package nl.weeaboo.common;

import java.io.Serializable;
import java.util.Arrays;

public final class Rect2D implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Rect2D EMPTY = new Rect2D(0, 0, 0, 0);

	public final double x, y, w, h;

	private Rect2D(double x, double y, double w, double h) {
		if (w < 0 || Double.isInfinite(w) || Double.isNaN(w) || h < 0 || Double.isInfinite(h) || Double.isNaN(h)) {
			throw new IllegalArgumentException("Dimensions must be >= 0 and finite, w=" + w + ", h=" + h);
		}

		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static Rect2D of(double x, double y, double w, double h) {
		return new Rect2D(x, y, w, h);
	}

	//Functions
    public Rect2D translatedCopy(double dx, double dy) {
        return Rect2D.of(x + dx, y + dy, w, h);
    }

	public static Rect2D combine(Rect2D... r) {
		if (r.length == 0) {
			return EMPTY;
		}

		double minX = r[0].x;
		double minY = r[0].y;
		double maxX = r[0].x + r[0].w;
		double maxY = r[0].y + r[0].h;
		for (int n = 1; n < r.length; n++) {
			minX = Math.min(minX, r[n].x);
			minY = Math.min(minY, r[n].y);
			maxX = Math.max(maxX, r[n].x+r[n].w);
			maxY = Math.max(maxY, r[n].y+r[n].h);
		}

		return Rect2D.of(minX, minY, maxX-minX, maxY-minY);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new double[] {x, y, w, h});
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rect2D) {
			Rect2D dim = (Rect2D)obj;
			return x == dim.x && y == dim.y && w == dim.w && h == dim.h;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Rect2D(" + x + "," + y + "," + w + "," + h + ")";
	}

	public Area2D toArea2D() {
		return Area2D.of(x, y, w, h);
	}

	//Getters
	public boolean contains(double px, double py) {
		return px >= x && px < x + w && py >= y && py < y + h;
	}

	public boolean contains(double rx, double ry, double rw, double rh) {
		if (w <= 0 || h <= 0) {
			return false;
		}
		return rx >= x && ry >= y && rx+rw <= x+w && ry+rh <= y+h;
	}

	public boolean intersects(double rx, double ry, double rw, double rh) {
		if (w <= 0 || h <= 0) {
			return false;
		}
		return rx + rw > x && ry + rh > y && rx < x + w && ry < y + h;
	}

	//Setters

}
