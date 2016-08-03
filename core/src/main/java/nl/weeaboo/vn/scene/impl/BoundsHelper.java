package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.impl.TransientListenerSupport;

public final class BoundsHelper extends TransientListenerSupport {

	private static final long serialVersionUID = 1L;

	private double x, y, w, h;
    private transient Rect2D cachedBounds;

	public double getX() { return x; }
	public double getY() { return y; }
	public double getWidth() { return w; }
	public double getHeight() { return h; }

	public Rect2D getBounds() {
        if (cachedBounds == null) {
            double w = getWidth();
            double h = getHeight();
            cachedBounds = Rect2D.of(x, y, Double.isNaN(w) ? 0 : w, Double.isNaN(h) ? 0 : h);
        }
        return cachedBounds;
	}

	public boolean contains(double px, double py) {
		return getBounds().contains(px, py);
	}

	public void setPos(double x, double y) {
        setBounds(x, y, this.w, this.h);
	}

	public void setSize(double w, double h) {
        setBounds(this.x, this.y, w, h);
	}

    public void setBounds(Rect2D rect) {
        setBounds(rect.x, rect.y, rect.w, rect.h);
    }

    public void setBounds(double x, double y, double w, double h) {
        Checks.checkRange(x, "x");
        Checks.checkRange(y, "y");
        Checks.checkRange(w, "w", 0);
        Checks.checkRange(h, "h", 0);

        if (this.x != x || this.y != y || this.w != w || this.h != h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            cachedBounds = null;
            fireListeners();
        }
    }

}
