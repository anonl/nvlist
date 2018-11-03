package nl.weeaboo.vn.image;

import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.test.RectAssert;

public final class TextureTest {

    @Test
    public void testDefaultUV() {
        RectAssert.assertEquals(Area2D.of(0, 0, 1, 1), ITexture.DEFAULT_UV, 0.0);
    }

}
