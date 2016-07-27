package nl.weeaboo.vn.text.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.scene.impl.BoundsHelper;
import nl.weeaboo.vn.text.IClickIndicator;

public class ClickIndicator implements IClickIndicator {

    private static final long serialVersionUID = 1L;

    private boolean destroyed;

    private final BoundsHelper bounds = new BoundsHelper();
    private double alpha = 0.0;
    private short z;
    private IDrawable drawable;

    public ClickIndicator() {
        bounds.setSize(32, 32);
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private void checkDrawableDestroyed() {
        if (drawable != null && drawable.isDestroyed()) {
            // Set drawable to null if destroyed
            drawable = null;
        }
    }

    @Override
    public void update(ITextDrawable td) {
        /**
         * Assumptions:
         * <ul>
         * <li>The drawable lies in the same coordinate system as the text drawable (same parent group)
         * <li>The text drawable isn't scaled or rotated
         * </ul>
         */
        Rect2D vb = td.getVisualBounds();
        bounds.setPos(vb.x + vb.w, vb.y);
        if (td.isVisible() && td.getMaxVisibleText() > 0 && td.getVisibleText() >= td.getMaxVisibleText()) {
            alpha = td.getAlpha();
        } else {
            alpha = 0.0;
        }
        z = td.getZ();

        updateDrawable();
    }

    protected void updateDrawable() {
        checkDrawableDestroyed();
        if (drawable == null) {
            return;
        }

        drawable.setAlpha(alpha);
        Rect2D r = bounds.getBounds();
        drawable.setBounds(r.x, r.y, r.w, r.h);
        drawable.setZ(z);
    }

    @Override
    public IDrawable getDrawable() {
        checkDrawableDestroyed();
        return drawable;
    }

    @Override
    public void setDrawable(IDrawable d) {
        checkDrawableDestroyed();
        if (drawable != d) {
            drawable = d;

            updateDrawable();
        }
    }

    @Override
    public double getWidth() {
        return bounds.getWidth();
    }

    @Override
    public double getHeight() {
        return bounds.getHeight();
    }

    @Override
    public void setSize(double w, double h) {
        bounds.setSize(w, h);

        updateDrawable();
    }

}
