package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;

class BoundsHelper extends ChangeHelper {

	private static final long serialVersionUID = 1L;

	private double x, y, w, h;

	// Functions

	// Getters
	public double getX() { return x; }
	public double getY() { return y; }
	public double getWidth() { return w; }
	public double getHeight() { return h; }

	public Rect2D getBounds() {
		double w = getWidth();
		double h = getHeight();
		return Rect2D.of(x, y, Double.isNaN(w) ? 0 : w, Double.isNaN(h) ? 0 : h);
	}

	public boolean contains(double px, double py) {
		return getBounds().contains(px, py);
	}

	// Setters
	public void setPos(double x, double y) {
		Checks.checkRange(x, "x");
		Checks.checkRange(y, "y");

		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;

			fireChanged();
		}
	}

	public void setSize(double w, double h) {
	    Checks.checkRange(w, "w", 0);
	    Checks.checkRange(h, "h", 0);

		if (this.w != w || this.h != h) {
			this.w = w;
			this.h = h;

			fireChanged();
		}
	}

}
