package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.io.Resources;

import nl.weeaboo.gdx.test.pixmap.PixmapEquality;

public final class PixmapTester implements Disposable {

    private boolean generate = false;

    private final PixmapEquality pixmapEquals = new PixmapEquality();
    private final List<Pixmap> allocatedPixmaps = new ArrayList<>();

    @Override
    public void dispose() {
        allocatedPixmaps.forEach(Pixmap::dispose);
        allocatedPixmaps.clear();
    }

    /**
     * Loads a pixmap from the /img test resource folder.
     * <p>
     * You don't need to dispose the pixmap itself as long as you call {@link PixmapTester#dispose()} at the
     * end of the test.
     */
    public Pixmap load(String filename) {
        try {
            byte[] bytes = Resources.toByteArray(getClass().getResource("/img/" + filename));
            Pixmap loaded = PixmapLoader.load(bytes, 0, bytes.length);
            allocatedPixmaps.add(loaded);
            return loaded;
        } catch (IOException ioe) {
            throw new AssertionError("Error loading pixmap: " + filename, ioe);
        }
    }

    /**
     * Creates a solid-colored pixmap.
     * <p>
     * You don't need to dispose the pixmap itself as long as you call {@link PixmapTester#dispose()} at the
     * end of the test.
     */
    public Pixmap newPixmap(Format format, Color color) {
        Pixmap pixmap = PixmapUtil.newUninitializedPixmap(3, 3, format);
        pixmap.setColor(color);
        pixmap.fill();
        allocatedPixmaps.add(pixmap);
        return pixmap;
    }

    /**
     * @see PixmapEquality#setMaxColorDiff(int)
     */
    public void setMaxColorDiff(int maxDiff) {
        pixmapEquals.setMaxColorDiff(maxDiff);
    }

    /**
     * @see PixmapEquality#assertEquals(Pixmap, Pixmap)
     */
    public void assertEquals(Pixmap expected, Pixmap actual) {
        pixmapEquals.assertEquals(expected, actual);
    }

    /**
     * Compares a pixmap with a previously rendered result image stored as a test resource.
     */
    public void checkRenderResult(String testName, Pixmap actual) {
        String outputPath = "src/test/resources/render/" + testName + ".png";
        FileHandle fileHandle = Gdx.files.local(outputPath);
        if (generate) {
            PixmapIO.writePNG(fileHandle, actual);
        } else {
            Pixmap expected = new Pixmap(fileHandle);
            pixmapEquals.assertEquals(expected, actual);
        }
    }

}
