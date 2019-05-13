package nl.weeaboo.vn.buildtools.optimizer.image;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.optimizer.ImageWithDefTester;
import nl.weeaboo.vn.gdx.graphics.PixmapTester;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageResizerTest {

    private final Dim baseRes = Dim.of(100, 100);
    private final Dim targetRes = Dim.of(50, 25);

    private PixmapTester pixmapTester;
    private ImageResizer resizer;

    private ImageWithDef imageA;

    @Before
    public void before() {
        HeadlessGdx.init();

        pixmapTester = new PixmapTester();
        resizer = new ImageResizer(baseRes, targetRes);

        // Two sub-rects: [0]=top half, [1]=bottom half (negative width/height)
        imageA = ImageWithDefTester.fromPixmap(pixmapTester.load(getClass(), "a.png"),
                Area.of(0, 0, 640, 180), Area.of(640, 360, -640, -180));
    }

    @After
    public void after() {
        DisposeUtil.dispose(pixmapTester);
    }

    /**
     * Resize an image that has some sub-rects defined. The image itself should be properly scaled, and the
     * sub-rects in the output should correspond to the new scaled coordinates as well.
     */
    @Test
    public void testScaleWithSubRects() {
        ImageWithDef resized = resizer.process(imageA);

        // Check that the resized pixmap was correctly rendered
        pixmapTester.checkRenderResult("testScaleWithSubRects", resized.getPixmap());

        // Also check the sub-rects
        // - scale factors are (50%, 25%)
        assertSubRect(resized, "r0", Area.of(0, 0, 320, 45));
        assertSubRect(resized, "r1", Area.of(320, 90, -320, -45));
    }

    private void assertSubRect(ImageWithDef imageWithDef, String subRectId, Area expectedArea) {
        IImageSubRect subRect = imageWithDef.getDef().findSubRect(subRectId);
        Assert.assertEquals(expectedArea, subRect.getArea());
    }

}
