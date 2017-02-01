package nl.weeaboo.vn.impl.image;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatch.EArea;
import nl.weeaboo.vn.impl.image.NinePatch;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

public class NinePatchTest {

    /** Check state after using the default constructor */
    @Test
    public void emptyNinePatch() {
        NinePatch ninePatch = new NinePatch();
        NinePatchAssert.assertNativeSize(ninePatch, 0, 0);
        CoreTestUtil.assertEquals(Insets2D.EMPTY, ninePatch.getInsets());
        // All textures null
        for (EArea area : EArea.values()) {
            Assert.assertNull(ninePatch.getTexture(area));
        }
    }

    /** Call a bunch of setters to see if they work */
    @Test
    public void setters() {
        TestTexture testTexture = new TestTexture();

        NinePatch ninePatch = new NinePatch(testTexture);
        Assert.assertEquals(testTexture, ninePatch.getTexture(EArea.CENTER));
        Assert.assertEquals(null, ninePatch.getTexture(EArea.TOP_LEFT));
        Assert.assertEquals(null, ninePatch.getTexture(EArea.BOTTOM_RIGHT));
        CoreTestUtil.assertEquals(Insets2D.EMPTY, ninePatch.getInsets());

        // Set insets
        ninePatch.setInsets(Insets2D.of(1, 2, 3, 4));
        CoreTestUtil.assertEquals(Insets2D.of(1, 2, 3, 4), ninePatch.getInsets());

        // Set some other textures
        ninePatch.setTexture(EArea.CENTER, null); // Seting to null removes the texture
        ninePatch.setTexture(EArea.TOP_LEFT, testTexture);
        ninePatch.setTexture(EArea.BOTTOM_RIGHT, testTexture);
        Assert.assertEquals(null, ninePatch.getTexture(EArea.CENTER));
        Assert.assertEquals(testTexture, ninePatch.getTexture(EArea.TOP_LEFT));
        Assert.assertEquals(testTexture, ninePatch.getTexture(EArea.BOTTOM_RIGHT));

        // Test set() method
        NinePatch copy = new NinePatch();
        copy.set(ninePatch);
        NinePatchAssert.assertEquals(ninePatch, copy);
    }

}
