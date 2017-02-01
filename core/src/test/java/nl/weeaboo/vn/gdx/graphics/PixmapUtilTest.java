package nl.weeaboo.vn.gdx.graphics;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.impl.image.TestImageUtil;

public class PixmapUtilTest {

    @Before
    public void before() {
        HeadlessGdx.init();
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
        TestImageUtil.assertEquals(expected, flip);

        flip.dispose();
        expected.dispose();
    }

}
