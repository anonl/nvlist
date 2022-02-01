package nl.weeaboo.vn.impl.text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.io.Resources;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;

public class GdxFontStoreTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private GdxFontStore fontStore;

    @Before
    public void before() {
        HeadlessGdx.init();

        fontStore = new GdxFontStore(new InternalGdxFileSystem(tempFolder.getRoot().getPath() + "/"));
    }

    @Test
    public void loadInvalid() {
        // Returns the default font if the requested font isn't found
        Assert.assertNotNull(getFont("doesntexist", TextStyle.defaultInstance()));
    }

    /**
     * Depending on the font style (bold and/or italic) a different font file may be loaded.
     */
    @Test
    public void loadStyleSpecific() throws IOException {
        extractFile("/font/RobotoSlab.ttf", "default.ttf");
        extractFile("/font/RobotoSlab.ttf", "default-bold.ttf");
        extractFile("/font/RobotoSlab.ttf", "default-italic.ttf");
        extractFile("/font/RobotoSlab.ttf", "default-bolditalic.ttf");

        IFontMetrics bold = getFont("default-bold.ttf", TextStyle.defaultInstance());
        assertFontEquals(bold, getFont("default.ttf", TextStyle.BOLD));

        IFontMetrics italic = getFont("default-italic.ttf", TextStyle.defaultInstance());
        assertFontEquals(italic, getFont("default.ttf", TextStyle.ITALIC));

        IFontMetrics boldItalic = getFont("default-bolditalic.ttf", TextStyle.defaultInstance());
        assertFontEquals(boldItalic, getFont("default.ttf", TextStyle.BOLD_ITALIC));
    }

    private IFontMetrics getFont(String path, TextStyle style) {
        return fontStore.getFontMetrics(FilePath.of(path), style);
    }

    private void extractFile(String srcResourcePath, String dstFileName) throws IOException {
        byte[] bytes = Resources.toByteArray(getClass().getResource(srcResourcePath));
        File file = new File(tempFolder.getRoot(), "font/" + dstFileName);
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), bytes);
    }

    private static void assertFontEquals(IFontMetrics expected, IFontMetrics actual) {
        Assert.assertEquals(extractFont(expected), extractFont(actual));
    }

    private static String extractFont(IFontMetrics metrics) {
        try {
            Field gdxFontField = metrics.getClass().getDeclaredField("font");
            gdxFontField.setAccessible(true);
            Object gdxFont = gdxFontField.get(metrics);

            Field bitmapFontField = gdxFont.getClass().getDeclaredField("bitmapFont");
            bitmapFontField.setAccessible(true);
            return ((BitmapFont)bitmapFontField.get(gdxFont)).getData().name;
        } catch (ReflectiveOperationException e) {
            throw new LinkageError("Error accessing internal of bitmap font class", e);
        }
    }
}
