package nl.weeaboo.common;

import java.io.Serializable;

public final class Rect implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Rect EMPTY = new Rect(0, 0, 0, 0);

	public final int x, y, w, h;

	private Rect(int x, int y, int w, int h) {
		if (w < 0 || h < 0) {
			throw new IllegalArgumentException("Dimensions must be >= 0, w=" + w + ", h=" + h);
		}

		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static Rect of(int x, int y, int w, int h) {
		if (x == 0 && y == 0 && w == 0 && h == 0) {
			return EMPTY;
		}
		return new Rect(x, y, w, h);
	}

	// Functions
    public Rect translatedCopy(int dx, int dy) {
        return Rect.of(x + dx, y + dy, w, h);
    }

	public static Rect combine(Rect... r) {
		if (r.length == 0) {
			return EMPTY;
		}

		int minX = r[0].x;
		int minY = r[0].y;
		int maxX = r[0].x + r[0].w;
		int maxY = r[0].y + r[0].h;
		for (int n = 1; n < r.length; n++) {
			minX = Math.min(minX, r[n].x);
			minY = Math.min(minY, r[n].y);
			maxX = Math.max(maxX, r[n].x + r[n].w);
			maxY = Math.max(maxY, r[n].y + r[n].h);
		}

		return new Rect(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public int hashCode() {
		return ((x << 16) ^ y) ^ ((w << 16) ^ h);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rect) {
			Rect r = (Rect) obj;
			return x == r.x && y == r.y && w == r.w && h == r.h;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Rect(" + x + ", " + y + ", " + w + ", " + h + ")";
	}

	public Area toArea() {
		return Area.of(x, y, w, h);
	}

	public Area2D toArea2D() {
		return Area2D.of(x, y, w, h);
	}

	public Rect2D toRect2D() {
		return Rect2D.of(x, y, w, h);
	}

	// Getters
	public boolean contains(double px, double py) {
		return px >= x && px < x + w && py >= y && py < y + h;
	}

	public boolean contains(double rx, double ry, double rw, double rh) {
		if (w <= 0 || h <= 0) {
			return false;
		}
		return rx >= x && ry >= y && rx + rw <= x + w && ry + rh <= y + h;
	}

	public boolean intersects(double rx, double ry, double rw, double rh) {
		if (w <= 0 || h <= 0) {
			return false;
		}
		return rx + rw > x && ry + rh > y && rx < x + w && ry < y + h;
	}

	// Setters

}
