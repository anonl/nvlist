package nl.weeaboo.vn.render;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.ApiTestUtil;

public class RenderUtilTest {

    private static final double EPSILON = ApiTestUtil.EPSILON;

    @Test
    public void roundClipRect() {
        Rect2D original = Rect2D.of(0.1, 0.9, 1.9, 1.4);
        Rect r = RenderUtil.roundClipRect(original);

        // Rounded rect must fit entirely inside the original rect
        Assert.assertEquals(Rect.of(1, 1, 1, 1), r);
        Assert.assertTrue(original.contains(r.x, r.y, r.w, r.h));
    }

    @Test
    public void premultiplyAlpha() {
        assertColorEquals("ff224466", RenderUtil.premultiplyAlpha(0xFF224466));
        assertColorEquals("7f112233", RenderUtil.premultiplyAlpha(0x7f224466));
        assertColorEquals("80112233", RenderUtil.premultiplyAlpha(0x80224466));
        assertColorEquals("00000000", RenderUtil.premultiplyAlpha(0x00224466));
    }

    @Test
    public void unPremultiplyAlpha() {
        assertColorEquals("ff224466", RenderUtil.unPremultiplyAlpha(0xFF224466));
        assertColorEquals("7f224466", RenderUtil.unPremultiplyAlpha(0x7f112233));
        assertColorEquals("80224466", RenderUtil.unPremultiplyAlpha(0x80112233));
        assertColorEquals("00000000", RenderUtil.unPremultiplyAlpha(0x00000000));
    }

    @Test
    public void argb2rgba() {
        assertColorEquals("22334411", RenderUtil.argb2rgba(0x11223344));
        assertColorEquals("112233ff", RenderUtil.argb2rgba(0xFF112233));
    }

    @Test
    public void rgba2argb() {
        assertColorEquals("11223344", RenderUtil.rgba2argb(0x22334411));
        assertColorEquals("ff112233", RenderUtil.rgba2argb(0x112233FF));
    }

    @Test
    public void packRGBAtoARGB() {
        assertColorEquals("20ff8040", RenderUtil.packRGBAtoARGB(1.0, .5, .25, .125));
        // Check that values are clipped when out of range
        assertColorEquals("00ff8040", RenderUtil.packRGBAtoARGB(2.0, .5, .25, -.123));
    }

    @Test
    public void interpolateColors() {
        final int a = 0x80604020;
        final int b = 0x20406080;
        Assert.assertEquals(a, RenderUtil.interpolateColors(a, b, 0.0f));
        Assert.assertEquals(0x50505050, RenderUtil.interpolateColors(a, b, 0.5f));
        Assert.assertEquals(b, RenderUtil.interpolateColors(a, b, 1.0f));
    }

    @Test
    public void relativeUV() {
        Area2D base = Area2D.of(0.2, 0.3, 0.4, 0.6);
        Area2D sub = Area2D.of(0.1, 0.2, 0.3, 0.4);
        Area2D combined = RenderUtil.combineUV(base, sub);

        Area2D expected = Area2D.of(
                base.x + 0.1 * base.w,
                base.y + 0.2 * base.h,
                0.3 * base.w,
                0.4 * base.h);

        RectAssert.assertEquals(expected, combined, EPSILON);
    }

    private static void assertColorEquals(String expectedHex, int color) {
        Assert.assertEquals(expectedHex, StringUtil.formatRoot("%08x", color));
    }

}
