package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Test;

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
            }
        }

        // Calling with a null texture always returns (0, 0)
        LvnTestUtil.assertEquals(0, 0, AlignUtil.getAlignOffset(null, 0.22, .77), EPSILON);
    }

}
