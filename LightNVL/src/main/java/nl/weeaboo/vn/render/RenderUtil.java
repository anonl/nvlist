package nl.weeaboo.vn.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;

public final class RenderUtil {

	private RenderUtil() {
	}

	public static Rect roundClipRect(Rect2D clip2D) {
		//Rounded to ints, resulting clip rect should be no bigger than the non-rounded version.
		int x0 = (int)Math.ceil(clip2D.x);
		int y0 = (int)Math.ceil(clip2D.y);

		//We can't just floor() the w/h because the ceil() of the x/y would skew the result.
		int x1 = (int)Math.floor(clip2D.x+clip2D.w);
		int y1 = (int)Math.floor(clip2D.y+clip2D.h);

		return Rect.of(x0, y0, Math.max(0, x1-x0), Math.max(0, y1-y0));
	}

	public static Area2D combineUV(Area2D uv, Area2D texUV) {
		return Area2D.of(texUV.x + uv.x * texUV.w, texUV.y + uv.y * texUV.h, texUV.w * uv.w, texUV.h * uv.h);
	}

	public static int interpolateColors(int c0, int c1, float w) {
        if (w >= 1) return c0;
        if (w <= 0) return c1;

        int a = interpolateColor((c0>>24) & 0xFF, (c1>>24) & 0xFF, w);
        int r = interpolateColor((c0>>16) & 0xFF, (c1>>16) & 0xFF, w);
        int g = interpolateColor((c0>>8 ) & 0xFF, (c1>>8 ) & 0xFF, w);
        int b = interpolateColor((c0    ) & 0xFF, (c1    ) & 0xFF, w);
        return (a<<24)|(r<<16)|(g<<8)|(b);
    }

    private static int interpolateColor(int a, int b, float w) {
        return Math.max(0, Math.min(255, Math.round(b - (b-a) * w)));
    }

    public static int premultiplyAlpha(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.max(0, Math.min(255, a * ((argb>>16)&0xFF) / 255));
        int g = Math.max(0, Math.min(255, a * ((argb>> 8)&0xFF) / 255));
        int b = Math.max(0, Math.min(255, a * ((argb    )&0xFF) / 255));
        return (a<<24)|(r<<16)|(g<<8)|(b);
    }

    public static int unPremultiplyAlpha(int argb) {
        int a = (argb >> 24) & 0xFF;
        if (a == 0) {
            return 0;
        }

        int r = Math.max(0, Math.min(255, 255 * ((argb>>16)&0xFF) / a));
        int g = Math.max(0, Math.min(255, 255 * ((argb>> 8)&0xFF) / a));
        int b = Math.max(0, Math.min(255, 255 * ((argb    )&0xFF) / a));
        return (a<<24)|(r<<16)|(g<<8)|(b);
    }

    public static int toABGR(int argb) {
        return (argb&0xFF000000) | ((argb<<16)&0xFF0000) | (argb&0xFF00) | ((argb>>16)&0xFF);
    }
    public static int toARGB(int abgr) {
        return (abgr&0xFF000000) | ((abgr<<16)&0xFF0000) | (abgr&0xFF00) | ((abgr>>16)&0xFF);
    }

}
