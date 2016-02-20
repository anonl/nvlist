package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IRenderEnv;

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
		this.glScreenVirtualBounds = RenderUtil.calculateGLScreenVirtualBounds(rclip.x, rclip.y,
				rscreen.w, rscreen.h, scale);
	}

    public static RenderEnv newDefaultInstance(Dim vsize, boolean isTouchScreen) {
        Rect rclip = Rect.of(0, 0, vsize.w, vsize.h);
        Dim rscreen = new Dim(vsize.w, vsize.h);
        return new RenderEnv(vsize, rclip, rscreen, isTouchScreen);
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
