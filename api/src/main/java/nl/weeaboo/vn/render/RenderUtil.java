package nl.weeaboo.vn.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;

public final class RenderUtil {

    private RenderUtil() {
    }

    /**
     * Returns the largest {@link Rect} that completely fits inside the supplied {@link Rect2D}.
     */
    public static Rect roundClipRect(Rect2D clip2D) {
        //Rounded to ints, resulting clip rect should be no bigger than the non-rounded version.
        int x0 = (int)Math.ceil(clip2D.x);
        int y0 = (int)Math.ceil(clip2D.y);

        //We can't just floor() the w/h because the ceil() of the x/y would skew the result.
        int x1 = (int)Math.floor(clip2D.x + clip2D.w);
        int y1 = (int)Math.floor(clip2D.y + clip2D.h);

        return Rect.of(x0, y0, Math.max(0, x1 - x0), Math.max(0, y1 - y0));
    }

    /**
     * Returns a sub-rectangle of the given base UV area
     *
     * @param baseUV The base UV rectangle.
     * @param uv The relative sub-rectangle within the base UV area.
     */
    public static Area2D combineUV(Area2D baseUV, Area2D uv) {
        return Area2D.of(
                baseUV.x + uv.x * baseUV.w,
                baseUV.y + uv.y * baseUV.h,
                baseUV.w * uv.w,
                baseUV.h * uv.h);
    }

    /**
     * Performs linear interpolation between two colors.
     *
     * @param w Determines the relative weight of each color, where 0.0 returns {@code c0} and 1.0 returns
     *        {@code c1}.
     */
    public static int interpolateColors(int c0, int c1, float w) {
        if (w <= 0) {
            return c0;
        } else if (w >= 1) {
            return c1;
        }

        int a = interpolateColor((c0 >> 24) & 0xFF, (c1 >> 24) & 0xFF, w);
        int r = interpolateColor((c0 >> 16) & 0xFF, (c1 >> 16) & 0xFF, w);
        int g = interpolateColor((c0 >> 8 ) & 0xFF, (c1 >> 8 ) & 0xFF, w);
        int b = interpolateColor((c0      ) & 0xFF, (c1      ) & 0xFF, w);
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    private static int interpolateColor(int a, int b, float w) {
        return Math.max(0, Math.min(255, Math.round(a + (b - a) * w)));
    }

    /**
     * Multiplies the RGB color components with the alpha, resulting in so-called premuliplied alpha.
     * <p>
     * Note that this operation is lossy due to rounding.
     */
    public static int premultiplyAlpha(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (a * ((argb >> 16) & 0xFF) + 127) / 255;
        int g = (a * ((argb >>  8) & 0xFF) + 127) / 255;
        int b = (a * ((argb      ) & 0xFF) + 127) / 255;
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    /**
     * Performs the opposite operation of {@link #premultiplyAlpha(int)}.
     * <p>
     * Note that these operations are lossy due to rounding errors.
     */
    public static int unPremultiplyAlpha(int argb) {
        int a = (argb >> 24) & 0xFF;
        if (a == 0) {
            return 0;
        }

        int round = a / 2;
        int r = (255 * ((argb >> 16) & 0xFF) + round) / a;
        int g = (255 * ((argb >> 8 ) & 0xFF) + round) / a;
        int b = (255 * ((argb      ) & 0xFF) + round) / a;
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    /**
     * Converts AARRGGBB to RRGGBBAA.
     */
    public static int argb2rgba(int argb) {
        return ((argb << 8) & 0xFFFFFF00) | ((argb >> 24) & 0xFF);
    }

    /**
     * Converts RRGGBBAA to AARRGGBB.
     */
    public static int rgba2argb(int rgba) {
        return ((rgba & 0xFF) << 24) | ((rgba >> 8) & 0xFFFFFF);
    }

    /**
     * Packs separate R,G,B,A doubles in the range {@code [0, 1]} into a single AARRGGBB int.
     */
    public static int packRGBAtoARGB(double r, double g, double b, double a) {
        int ri = Math.max(0, Math.min(255, (int)Math.round(r * 255)));
        int gi = Math.max(0, Math.min(255, (int)Math.round(g * 255)));
        int bi = Math.max(0, Math.min(255, (int)Math.round(b * 255)));
        int ai = Math.max(0, Math.min(255, (int)Math.round(a * 255)));
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

}
