package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;
import nl.weeaboo.vn.gdx.HeadlessGdx;

public final class PremultUtilTest {

    private static final Logger LOG = LoggerFactory.getLogger(PremultUtilTest.class);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final PixmapTester pixmapTester = new PixmapTester();

    @Before
    public void before() {
        HeadlessGdx.init();
    }

    // Note: PremultUtil.premultiplyAlpha() is tested in PixmapPremultiplyTest

    @Test
    public void testWritePremultPng() throws IOException {
        FileHandle file = Gdx.files.absolute(tempFolder.newFile().getAbsolutePath());

        Color[] colors = {new Color(.5f, .5f, .5f, .5f), Color.CLEAR};
        for (Format format : Format.values()) {
            for (Color color : colors) {
                LOG.info("Write premult PNG: format={}, color={}", format, color);

                Pixmap original = pixmapTester.newPixmap(format, color);
                PremultUtil.writePremultPng(file, original);

                /*
                 * Decode the PNG that we wrote, convert it to premultiplied alpha and the same format as the
                 * original to be able to compare the two pixmaps
                 */
                byte[] bytes = file.readBytes();
                Pixmap loaded = new Pixmap(bytes, 0, bytes.length);
                PremultUtil.premultiplyAlpha(loaded);
                loaded = PixmapUtil.convert(loaded, format, true);

                // The decoded pixmap should be equivalent (excluding small rounding errors)
                PixmapEquality pixmapEquals = new PixmapEquality();
                pixmapEquals.setMaxColorDiff(2);
                pixmapEquals.assertEquals(original, loaded);
            }
        }
    }

}
