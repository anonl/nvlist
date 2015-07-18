package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.LvnTestUtil;
import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.image.impl.TestTexture;
import nl.weeaboo.vn.math.Vec2;

public class AlignUtilTest {

    private static final double EPSILON = NvlTestUtil.EPSILON;
    private final TestTexture tex = new TestTexture(20, 10);

    @Test
    public void alignOffset() {
        Assert.assertEquals(0, AlignUtil.getAlignOffset(100, 0), EPSILON);
        Assert.assertEquals(-50, AlignUtil.getAlignOffset(100, .5), EPSILON);
        Assert.assertEquals(-100, AlignUtil.getAlignOffset(100, 1), EPSILON);
    }

    @Test
    public void alignOffsetTexture() {
        // Check that the x/y align for textures is consistent with separate calls to the align function
        for (double alignX = -1.0; alignX <= 1.0; alignX += 1.0) {
            for (double alignY = -1.0; alignY <= 1.0; alignY += 1.0) {
                Vec2 v = AlignUtil.getAlignOffset(tex, alignX, alignY);
                Assert.assertEquals(v.x, AlignUtil.getAlignOffset(tex.getWidth(), alignX), EPSILON);
                Assert.assertEquals(v.y, AlignUtil.getAlignOffset(tex.getHeight(), alignY), EPSILON);


                Rect2D r = AlignUtil.getAlignedBounds(tex, alignX, alignY);
                LvnTestUtil.assertEquals(v.x, v.y, tex.getWidth(), tex.getHeight(), r);
            }
        }

        // Calling with a null texture always returns (0, 0)
        LvnTestUtil.assertEquals(0, 0, AlignUtil.getAlignOffset(null, 0.22, .77), EPSILON);
        LvnTestUtil.assertEquals(0, 0, 0, 0, AlignUtil.getAlignedBounds(null, 0.22, 0.77));
    }

    @Test
    public void alignSubRect() {
        Vec2 v = AlignUtil.alignSubRect(Rect2D.of(10, 10, 10, 10), 50, 40, 3);
        LvnTestUtil.assertEquals(30.0 / 50.0, 20.0 / 40.0, v, EPSILON);

        v = AlignUtil.alignSubRect(Rect2D.of(20, 5, 0, 15), 50, 40, 3);
        LvnTestUtil.assertEquals(30.0 / 50.0, 20.0 / 40.0, v, EPSILON);

        // Test all valid anchor positions
        Rect2D r = Rect2D.of(0, 0, 10, 10);
        LvnTestUtil.assertEquals(.00, .50, AlignUtil.alignSubRect(r, 20, 20, 1), EPSILON);
        LvnTestUtil.assertEquals(.25, .50, AlignUtil.alignSubRect(r, 20, 20, 2), EPSILON);
        LvnTestUtil.assertEquals(.50, .50, AlignUtil.alignSubRect(r, 20, 20, 3), EPSILON);
        LvnTestUtil.assertEquals(.00, .25, AlignUtil.alignSubRect(r, 20, 20, 4), EPSILON);
        LvnTestUtil.assertEquals(.25, .25, AlignUtil.alignSubRect(r, 20, 20, 5), EPSILON);
        LvnTestUtil.assertEquals(.50, .25, AlignUtil.alignSubRect(r, 20, 20, 6), EPSILON);
        LvnTestUtil.assertEquals(.00, .00, AlignUtil.alignSubRect(r, 20, 20, 7), EPSILON);
        LvnTestUtil.assertEquals(.25, .00, AlignUtil.alignSubRect(r, 20, 20, 8), EPSILON);
        LvnTestUtil.assertEquals(.50, .00, AlignUtil.alignSubRect(r, 20, 20, 9), EPSILON);
    }

}
