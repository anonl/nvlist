package nl.weeaboo.vn.render;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.ApiTestUtil;

public class RenderUtilTest {

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
        Assert.assertEquals("ff224466", Integer.toHexString(RenderUtil.premultiplyAlpha(0xFF224466)));
        Assert.assertEquals("7f112233", Integer.toHexString(RenderUtil.premultiplyAlpha(0x7f224466)));
        Assert.assertEquals("80112233", Integer.toHexString(RenderUtil.premultiplyAlpha(0x80224466)));
        Assert.assertEquals("0", Integer.toHexString(RenderUtil.premultiplyAlpha(0x00224466)));
    }

    @Test
    public void unPremultiplyAlpha() {
        Assert.assertEquals("ff224466", Integer.toHexString(RenderUtil.unPremultiplyAlpha(0xFF224466)));
        Assert.assertEquals("7f224466", Integer.toHexString(RenderUtil.unPremultiplyAlpha(0x7f112233)));
        Assert.assertEquals("80224466", Integer.toHexString(RenderUtil.unPremultiplyAlpha(0x80112233)));
        Assert.assertEquals("0", Integer.toHexString(RenderUtil.unPremultiplyAlpha(0x00000000)));
    }

    @Test
    public void argb2rgba() {
        Random random = new Random();
        for (int n = 0; n < 100; n++) {
            int argb = random.nextInt();
            int transformed = RenderUtil.toARGB(RenderUtil.toABGR(argb));
            Assert.assertEquals(Integer.toHexString(argb), Integer.toHexString(transformed));
        }
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

        ApiTestUtil.assertEquals(expected, combined);
    }

}
