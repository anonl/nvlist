package nl.weeaboo.vn.gdx.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.gdx.HeadlessGdx;

public class GdxGraphicsTestUtil {

    static {
        HeadlessGdx.init();
    }

    /**
     * Copies an array of ARGB8888 pixels to a pixmap.
     */
    public static void setPixmapPixels(Pixmap target, int[] argb) {
        int len = target.getWidth() * target.getHeight();
        Checks.checkArgument(len == argb.length,
                "int[] has incorrect length: " + argb.length + ", expected: " + len);

        int t = 0;
        for (int y = 0; y < target.getHeight(); y++) {
            for (int x = 0; x < target.getWidth(); x++) {
                target.drawPixel(x, y, argb[t++]);
            }
        }
    }

    /**
     * Creates a {@code w x h} RGBA8888 texture filled with a default color.
     */
    public static Texture createTestTexture(int w, int h) {
        Pixmap pixmap = PixmapUtil.newUninitializedPixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0xAA996633);
        pixmap.fill();

        return new Texture(pixmap);
    }
}

