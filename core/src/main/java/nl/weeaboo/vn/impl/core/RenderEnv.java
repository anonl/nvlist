package nl.weeaboo.vn.impl.core;

import com.google.common.primitives.Doubles;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.render.IRenderEnv;

public final class RenderEnv implements IRenderEnv {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final Dim virtualSize;
    private final Rect realClip;
    private final Dim realScreenSize;
    private final boolean isTouchScreen;

    private final double scale;
    private final Rect glClip;
    private final Rect2D glScreenVirtualBounds;

    /**
     * @param vsize Virtual screen size.
     * @param rclip Clipping rectangle into which the virtual screen is projected.
     * @param rscreen Physical size of the entire rendering viewport in which the clipping rectangle is
     *        contained.
     */
    public RenderEnv(Dim vsize, Rect rclip, Dim rscreen, boolean isTouchScreen) {
        this.virtualSize = vsize;
        this.realClip = rclip;
        this.realScreenSize = rscreen;
        this.isTouchScreen = isTouchScreen;

        this.scale = Math.min(rclip.w / (double)vsize.w, rclip.h / (double)vsize.h);
        this.glClip = Rect.of(rclip.x, rscreen.h - rclip.y - rclip.h, rclip.w, rclip.h);
        this.glScreenVirtualBounds = calculateGLScreenVirtualBounds(rclip.x, rclip.y,
                rscreen.w, rscreen.h, scale);
    }

    /**
     * Convenience constructor. Sets the clip/screen sizes to the vsize.
     */
    public static RenderEnv newDefaultInstance(Dim vsize, boolean isTouchScreen) {
        Rect rclip = Rect.of(0, 0, vsize.w, vsize.h);
        Dim rscreen = Dim.of(vsize.w, vsize.h);
        return new RenderEnv(vsize, rclip, rscreen, isTouchScreen);
    }

    /**
     * @param scale The scale factor from virtual coordinates to real coordinates.
     */
    private static Rect2D calculateGLScreenVirtualBounds(int clipX, int clipY, int screenWidth,
            int screenHeight, double scale) {
        double s = 1.0 / scale;
        if (!Doubles.isFinite(s)) {
            return Rect2D.EMPTY;
        }

        double x = s * -clipX;
        double y = s * -clipY;
        double w = s * screenWidth;
        double h = s * screenHeight;

        w = Double.isNaN(w) ? 0 : Math.max(0, w);
        h = Double.isNaN(h) ? 0 : Math.max(0, h);

        return Rect2D.of(x, y, w, h);
    }

    @Override
    public int getWidth() {
        return virtualSize.w;
    }

    @Override
    public int getHeight() {
        return virtualSize.h;
    }

    @Override
    public Dim getVirtualSize() {
        return virtualSize;
    }

    @Override
    public Rect getRealClip() {
        return realClip;
    }

    @Override
    public Dim getScreenSize() {
        return realScreenSize;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Rect getGLClip() {
        return glClip;
    }

    @Override
    public Rect2D getGLScreenVirtualBounds() {
        return glScreenVirtualBounds;
    }

    @Override
    public boolean isTouchScreen() {
        return isTouchScreen;
    }

}
