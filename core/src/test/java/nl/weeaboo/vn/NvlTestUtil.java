package nl.weeaboo.vn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.RenderEnv;
import nl.weeaboo.vn.image.impl.TestTexture;
import nl.weeaboo.vn.scene.impl.ImageDrawable;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.IScriptContext;

public final class NvlTestUtil {

	public static final IRenderEnv BASIC_ENV = new RenderEnv(new Dim(1280, 720), Rect.of(0, 75, 800, 450), new Dim(800, 600), false);

    private static final Logger LOG = LoggerFactory.getLogger(NvlTestUtil.class);
	public static final double EPSILON = 0.001;

	private NvlTestUtil() {
	}

    public static Screen newScreen() {
        Dim vsize = BASIC_ENV.getVirtualSize();
        return new Screen(Rect2D.of(0, 0, vsize.w, vsize.h), BASIC_ENV);
	}

	public static IScriptContext newScriptContext() {
	    return null;
	}

    public static ImageDrawable newImage() {
        ImageDrawable image = new ImageDrawable();
        image.setTexture(new TestTexture(2, 2));
        return image;
	}

	public static void configureLogger() {
		try {
            InputStream in = NvlTestUtil.class.getResourceAsStream("logging.debug.properties");
			if (in == null) {
				throw new FileNotFoundException();
			}
			try {
				LogManager.getLogManager().readConfiguration(in);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			LOG.warn("Unable to read logging config", e);
		}
	}

	public static void trySleep(long millis) {
	    try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
	}

}
