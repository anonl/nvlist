package nl.weeaboo.vn.gdx.graphics.blur;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.PremultUtil;
import nl.weeaboo.vn.gdx.graphics.blur.ImageBlur;

public class ImageBlurTest {

    // If set to true, generate images. If set to false, compare results to previously generated images.
    private boolean generate = true;

    private PixmapEquality pixmapEquals;
    private ImageBlur imageBlur;

    @Before
    public void before() {
        HeadlessGdx.init();

        pixmapEquals = new PixmapEquality();
        imageBlur = new ImageBlur();
    }

    /** Test various kernel sizes on a fully opaque image */
    @Test
    public void simpleBlurOpaque() {
        testBlur("img/a.png", "opaque");
    }

    /** Test various kernel sizes on a fully opaque image */
    @Test
    public void spriteBlur() {
        testBlur("img/arm01s.png", "sprite");
    }

    private void testBlur(String srcImagePath, String testName) {
        Pixmap original = loadImage(srcImagePath);
        Pixmap blurred = PixmapUtil.copy(original);

        try {
            PixmapUtil.copy(original, blurred);

            /*
             * Special cases to check:
             * - Zero
             * - Even and odd radius values
             * - Radius larger that the image width and/or height
             */
            for (int radius : new int[] {0, 1, 8, 999}) {
                imageBlur.setRadius(radius);
                imageBlur.applyBlur(blurred);

                checkRenderResult(testName + "-r" + imageBlur.getRadius(), blurred);
            }
        } finally {
            original.dispose();
            blurred.dispose();
        }
    }

    private static Pixmap loadImage(String path) {
        Pixmap pixmap = new Pixmap(Gdx.files.classpath(path));
        pixmap = PixmapUtil.convert(pixmap, Format.RGBA8888, true);
        PremultUtil.premultiplyAlpha(pixmap);
        return pixmap;
    }

    /**
     * @param actual Pixmap with premultiplied alpha.
     */
    public void checkRenderResult(String testName, Pixmap actual) {
        String outputPath = "src/test/resources/render/blur/" + testName + ".png";
        FileHandle fileHandle = Gdx.files.local(outputPath);
        if (generate) {
            PremultUtil.writePremultPng(fileHandle, actual);
        } else {
            Pixmap expected = new Pixmap(fileHandle);
            pixmapEquals.assertEquals(expected, actual);
        }
    }
}
