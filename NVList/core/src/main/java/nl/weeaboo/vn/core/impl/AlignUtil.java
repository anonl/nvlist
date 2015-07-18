package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Vec2;

public final class AlignUtil {

	private AlignUtil() {
	}

	public static Vec2 getAlignOffset(ITexture tex, double alignX, double alignY) {
		if (tex == null) {
			return new Vec2(0, 0);
		}
		return new Vec2(getAlignOffset(tex.getWidth(), alignX), getAlignOffset(tex.getHeight(), alignY));
	}

    /**
     * @return A relative offset (in pixels), based on the fractional alignment.
     */
	public static double getAlignOffset(double size, double align) {
        return -align * size;
	}

	public static Rect2D getAlignedBounds(ITexture tex, double alignX, double alignY) {
		if (tex == null) {
			return getAlignedBounds(0, 0, alignX, alignY);
		}
		return getAlignedBounds(tex.getWidth(), tex.getHeight(), alignX, alignY);
	}
	public static Rect2D getAlignedBounds(double w, double h, double alignX, double alignY) {
		return Rect2D.of(getAlignOffset(w, alignX), getAlignOffset(h, alignY), w, h);
	}

//	public static Rect2D getCombinedBounds(ITexture tex0, double alignX0, double alignY0,
//			ITexture tex1, double alignX1, double alignY1)
//	{
//		Rect2D r0 = getAlignedBounds(tex0, alignX0, alignY0);
//		Rect2D r1 = getAlignedBounds(tex1, alignX1, alignY1);
//		double x = Math.min(r0.x, r1.x);
//		double y = Math.min(r0.y, r1.y);
//		double w = Math.max(r0.x+r0.w-x, r1.x+r1.w-x);
//		double h = Math.max(r0.y+r0.h-y, r1.y+r1.h-y);
//		return Rect2D.of(x, y, Math.max(0, w), Math.max(0, h));
//	}

	public static double alignAnchorX(double outer, double inner, int anchor) {
		if (anchor == 2 || anchor == 5 || anchor == 8) {
			return (outer-inner) / 2;
		} else if (anchor == 3 || anchor == 6 || anchor == 9) {
			return (outer-inner);
		}
		return 0;
	}

	public static double alignAnchorY(double outer, double inner, int anchor) {
		if (anchor >= 4 && anchor <= 6) {
			return (outer-inner) / 2;
		} else if (anchor >= 1 && anchor <= 3) {
			return (outer-inner);
		}
		return 0;
	}

    /** Calculates the X/Y alignment values required to move the given sub-rect to the desired position */
    public static Vec2 alignSubRect(Rect2D subRect, double outerW, double outerH, int anchor) {
        Vec2 p = new Vec2(alignAnchorX(subRect.w, 0, anchor), alignAnchorY(subRect.h, 0, anchor));
        Vec2 offset = new Vec2(alignAnchorX(outerW, 0, anchor), alignAnchorY(outerH, 0, anchor));

        offset.x -= p.x + subRect.x;
        if (outerW != 0) {
            offset.x /= outerW;
        }

        offset.y -= p.y + subRect.y;
        if (outerH != 0) {
            offset.y /= outerH;
        }

		return offset;
	}

}
