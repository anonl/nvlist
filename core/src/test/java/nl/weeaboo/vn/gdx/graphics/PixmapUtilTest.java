package nl.weeaboo.vn.gdx.graphics;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.DisposeUtil;

public class PixmapUtilTest {

    private PixmapTester pixmapTester;

    @Before
    public void before() {
        HeadlessGdx.init();
        pixmapTester = new PixmapTester();
    }

    @After
    public void after() {
        DisposeUtil.dispose(pixmapTester);
    }

    @Test
    public void testFlipVertical() {
        Pixmap flip = new Pixmap(2, 3, Format.RGBA8888);
        flip.drawPixel(0, 0, 0xAABBCCDD);
        flip.drawPixel(1, 2, 0x11223344);
        PixmapUtil.flipVertical(flip);

        Pixmap expected = new Pixmap(2, 3, Format.RGBA8888);
        expected.drawPixel(0, 2, 0xAABBCCDD);
        expected.drawPixel(1, 0, 0x11223344);

        pixmapTester.assertEquals(expected, flip);

        flip.dispose();
        expected.dispose();
    }

    @Test
    public void testResizedCopy() {
        Pixmap pixmap = pixmapTester.load(getClass(), "/img/a.png");
        checkResizeResult(pixmap, Filter.BiLinear, "bilinear");
        checkResizeResult(pixmap, Filter.NearestNeighbour, "nearest");
    }

    @Test
    public void testHasAlpha() {
        Set<Format> formatsWithAlpha = ImmutableSet.of(
                Format.Alpha,
                Format.LuminanceAlpha,
                Format.RGBA4444,
                Format.RGBA8888
        );
        for (Format format : Format.values()) {
            Assert.assertEquals(formatsWithAlpha.contains(format), PixmapUtil.hasAlpha(format));
        }
    }

    @Test
    public void testHasTranslucentPixel() {
        // Create a pixmap with a format that allows translucency, but all pixels are opaque
        Pixmap pixmap = pixmapTester.newPixmap(Format.RGBA8888, Color.RED);
        Assert.assertFalse(PixmapUtil.hasTranslucentPixel(pixmap));

        // Set a pixel to transparent
        pixmap.setBlending(Blending.None);
        pixmap.drawPixel(0, 0, 0xffffff80);
        Assert.assertTrue(PixmapUtil.hasTranslucentPixel(pixmap));

        // Some formats don't support alpha at all, so trying to set its pixels to translucent doesn't work
        pixmap = pixmapTester.newPixmap(Format.RGB565, Color.CLEAR);
        Assert.assertFalse(PixmapUtil.hasTranslucentPixel(pixmap));
    }

    private void checkResizeResult(Pixmap pixmap, Filter filter, String testName) {
        Pixmap resized = PixmapUtil.resizedCopy(pixmap, Dim.of(720, 770), filter);
        try {
            pixmapTester.checkRenderResult("resize/" + testName, resized);
        } finally {
            resized.dispose();
        }
    }

}
