package nl.weeaboo.vn.impl.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.core.RenderEnv;
import nl.weeaboo.vn.impl.core.SkipState;
import nl.weeaboo.vn.impl.image.TestTexture;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.text.TestTextBoxState;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IScreenTextState;

public final class CoreTestUtil {

    public static final double EPSILON = 0.001;
    public static final IRenderEnv BASIC_ENV = new RenderEnv(Dim.of(1280, 720), Rect.of(0, 75, 800, 450),
            Dim.of(800, 600), false);

    private static final Logger LOG = LoggerFactory.getLogger(CoreTestUtil.class);

    private CoreTestUtil() {
    }

    /** Creates a new dummy screen. */
    public static Screen newScreen() {
        Dim vsize = BASIC_ENV.getVirtualSize();
        IScreenTextState textBoxState = new TestTextBoxState();
        SkipState skipState = new SkipState();
        return new Screen(Rect2D.of(0, 0, vsize.w, vsize.h), BASIC_ENV, textBoxState, skipState);
    }

    /** Creates a new image drawable, initialized with a dummy texture. */
    public static ImageDrawable newImage() {
        ImageDrawable image = new ImageDrawable();
        image.setTexture(new TestTexture());
        return image;
    }

    /** Configures the SLF4J logger with a configuration used for test execution. */
    public static void configureLogger() {
        try {
            InputStream in = CoreTestUtil.class.getResourceAsStream("logging.debug.properties");
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

    /**
     * Fuzzy equals for {@link Vec2}.
     */
    public static void assertEquals(double x, double y, Vec2 vec, double epsilon) {
        Assert.assertEquals(x, vec.x, epsilon);
        Assert.assertEquals(y, vec.y, epsilon);
    }

}
