package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.text.ITextRenderer;

final class RenderUtil {

	private RenderUtil() {
	}

	/**
	 * @param scale The scale factor from virtual coordinates to real coordinates.
	 */
	public static Rect2D calculateGLScreenVirtualBounds(int clipX, int clipY,
			int screenWidth, int screenHeight, double scale)
	{
		double s = 1.0 / scale;
		double x = s * -clipX;
		double y = s * -clipY;
		double w = s * screenWidth;
		double h = s * screenHeight;

		w = Double.isNaN(w) ? 0 : Math.max(0, w);
		h = Double.isNaN(h) ? 0 : Math.max(0, h);

		return Rect2D.of(x, y, w, h);
	}

    /**
     * @param outerW Width of the bounding rectangle within which the text should be positioned. 
     * @param outerH Height of the bounding rectangle within which the text should be positioned. 
     */
    public static Vec2 getTextRendererXY(double outerW, double outerH, ITextRenderer tr, double valign) {
        double x = 0;
        /*out.x = LayoutUtil.alignAnchorX(outerW, tr.getTextWidth(), anchor);
        if (tr.isRightToLeft()) {
            x -= tr.getTextTrailing();
        } else {
            x -= tr.getTextLeading();
        }*/
        double y = (outerH - tr.getTextHeight()) * valign;
        return new Vec2(x, y);
    }
}
