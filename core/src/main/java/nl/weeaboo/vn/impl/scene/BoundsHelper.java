package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.core.TransientListenerSupport;
import nl.weeaboo.vn.scene.IPositionable;

public final class BoundsHelper extends TransientListenerSupport implements IPositionable {

    private static final long serialVersionUID = 1L;

    private double x;
    private double y;
    private double w;
    private double h;
    private transient Rect2D cachedBounds;

    /**
     * @return The top-left x of the bounds.
     */
    public double getX() {
        return x;
    }

    /**
     * @return The top-left y of the bounds.
     */
    public double getY() {
        return y;
    }

    /**
     * @return The width of the bounds.
     */
    public double getWidth() {
        return w;
    }

    /**
     * @return The height of the bounds.
     */
    public double getHeight() {
        return h;
    }

    /**
     * @return The current bounds.
     */
    public Rect2D getBounds() {
        if (cachedBounds == null) {
            double w = getWidth();
            double h = getHeight();
            cachedBounds = Rect2D.of(x, y, Double.isNaN(w) ? 0 : w, Double.isNaN(h) ? 0 : h);
        }
        return cachedBounds;
    }

    /**
     * Checks if the specified point lies inside the bounds, or on its boundary.
     */
    public boolean contains(double px, double py) {
        return getBounds().contains(px, py);
    }

    @Override
    public final void setX(double x) {
        setPos(x, getY());
    }

    @Override
    public final void setY(double y) {
        setPos(getX(), y);
    }

    @Override
    public void translate(double dx, double dy) {
        setPos(getX() + dx, getY() + dy);
    }

    @Override
    public void setPos(double x, double y) {
        setBounds(x, y, this.w, this.h);
    }

    @Override
    public void setWidth(double w) {
        setSize(w, getHeight());
    }

    @Override
    public void setHeight(double h) {
        setSize(getWidth(), h);
    }

    @Override
    public void setSize(double w, double h) {
        setBounds(this.x, this.y, w, h);
    }

    /**
     * @see #setBounds(double, double, double, double)
     */
    public void setBounds(Rect2D rect) {
        setBounds(rect.x, rect.y, rect.w, rect.h);
    }

    @Override
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
