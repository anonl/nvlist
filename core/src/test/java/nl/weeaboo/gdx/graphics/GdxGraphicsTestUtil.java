package nl.weeaboo.gdx.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.common.Checks;
import nl.weeaboo.gdx.HeadlessGdx;

public class GdxGraphicsTestUtil {

    static {
        HeadlessGdx.init();
    }

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

    public static Texture createTestTexture(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0xAA996633);
        pixmap.fill();

        return new Texture(pixmap);
    }
}

