package nl.weeaboo.vn.impl.core;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Vec2;

public final class AlignUtil {

    private AlignUtil() {
    }

    /**
     * @return A relative offset (in pixels), based on the fractional alignment.
     */
    public static double getAlignOffset(double size, double align) {
        return -align * size;
    }

    /**
     * Returns the relative bounds for the given texture, if aligned using the given fractional alignment.
     */
    public static Rect2D getAlignedBounds(ITexture tex, double alignX, double alignY) {
        if (tex == null) {
            return getAlignedBounds(0, 0, alignX, alignY);
        }
        return getAlignedBounds(tex.getWidth(), tex.getHeight(), alignX, alignY);
    }

    /**
     * Returns the relative bounds for an object of the given dimensions, if aligned using the given fractional
     * alignment.
     */
    public static Rect2D getAlignedBounds(double w, double h, double alignX, double alignY) {
        return Rect2D.of(getAlignOffset(w, alignX), getAlignOffset(h, alignY), w, h);
    }

    /**
     * Calculates the relative x-position for {@code inner}, when aligning relative to {@code outer} using
     * {@code anchor} as its alignment.
     */
    public static double alignAnchorX(double outer, double inner, Direction anchor) {
        switch (anchor) {
        case TOP:
        case CENTER:
        case BOTTOM:
            return (outer - inner) / 2;
        case TOP_RIGHT:
        case RIGHT:
        case BOTTOM_RIGHT:
            return (outer - inner);
        default:
            return 0;
        }
    }

    /**
     * Calculates the relative y-position for {@code inner}, when aligning relative to {@code outer} using
     * {@code anchor} as its alignment.
     */
    public static double alignAnchorY(double outer, double inner, Direction anchor) {
        switch (anchor) {
        case LEFT:
        case CENTER:
        case RIGHT:
            return (outer - inner) / 2;
        case BOTTOM_LEFT:
        case BOTTOM:
        case BOTTOM_RIGHT:
            return (outer - inner);
        default:
            return 0;
        }
    }

    /** Calculates the X/Y alignment values required to move the given sub-rect to the desired position */
    public static Vec2 alignSubRect(Rect2D subRect, double outerW, double outerH, Direction anchor) {
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
