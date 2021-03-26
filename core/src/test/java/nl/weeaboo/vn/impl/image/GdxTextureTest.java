package nl.weeaboo.vn.impl.image;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.GdxGraphicsTestUtil;
import nl.weeaboo.vn.gdx.res.ResourceStub;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

public class GdxTextureTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    @Before
    public void before() {
        HeadlessGdx.init();
    }

    /** Check behavior when the internal resource returns {@code null} */
    @Test
    public void nullResource() {
        ResourceStub<TextureRegion> res = new ResourceStub<>();
        GdxTexture adapter = new GdxTexture(res, 2, 3);

        Assert.assertEquals(0, adapter.getHandle());
        Assert.assertEquals(null, adapter.getTexture());
        Assert.assertEquals(null, adapter.getTextureRegion());
        Assert.assertEquals(ITexture.DEFAULT_UV, adapter.getUV());
        assertPixelSize(adapter, 0, 0);
        assertScale(adapter, 2, 3);
        assertSize(adapter, 0, 0);
    }

    @Test
    public void textureRegion() {
        ResourceStub<TextureRegion> res = new ResourceStub<>();
        GdxTexture adapter = new GdxTexture(res, 1, 1);

        Texture tex = GdxGraphicsTestUtil.createTestTexture(20, 40);
        TextureRegion region = new TextureRegion(tex, 1, 2, 4, 8);
        res.set(region);
        adapter.setTextureRegion(res, 2);

        Assert.assertNotEquals(0, adapter.getHandle());
        Assert.assertEquals(tex, adapter.getTexture());
        Assert.assertEquals(region, adapter.getTextureRegion());
        RectAssert.assertEquals(Area2D.of(.05, .05, .2, .2), adapter.getUV(), EPSILON);
        assertPixelSize(adapter, 4, 8);
        assertScale(adapter, 2, 2);
        assertSize(adapter, 2 * 4, 2 * 8);
    }

    /** {@link GdxTexture#getTextureRegion(Area2D)} */
    @Test
    public void getSubRegion() {
        ResourceStub<TextureRegion> res = new ResourceStub<>();
        GdxTexture adapter = new GdxTexture(res, 1, 1);

        Texture tex = GdxGraphicsTestUtil.createTestTexture(10, 10);
        res.set(new TextureRegion(tex, 0, 0, 10, 10));

        TextureRegion subRegion = adapter.getTextureRegion(Area2D.of(.2, .3, .4, .5));
        Assert.assertNotNull(subRegion);
        Assert.assertEquals(.2, subRegion.getU(), EPSILON);
        Assert.assertEquals(.2 + .4, subRegion.getU2(), EPSILON);
        Assert.assertEquals(.3, subRegion.getV(), EPSILON);
        Assert.assertEquals(.3 + .5, subRegion.getV2(), EPSILON);
    }

    private static void assertSize(GdxTexture adapter, double w, double h) {
        Assert.assertEquals(w, adapter.getWidth(), EPSILON);
        Assert.assertEquals(h, adapter.getHeight(), EPSILON);
    }

    private static void assertPixelSize(GdxTexture adapter, int w, int h) {
        Assert.assertEquals(w, adapter.getPixelWidth());
        Assert.assertEquals(h, adapter.getPixelHeight());
    }

    private static void assertScale(GdxTexture adapter, double w, double h) {
        Assert.assertEquals(w, adapter.getScaleX(), EPSILON);
        Assert.assertEquals(h, adapter.getScaleY(), EPSILON);
    }

}
