package nl.weeaboo.vn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.LogManager;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.RenderEnv;
import nl.weeaboo.vn.image.impl.TestTexture;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.scene.IScreenTextState;
import nl.weeaboo.vn.scene.impl.ImageDrawable;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.text.impl.TestTextBoxState;

public final class CoreTestUtil {

    public static final double EPSILON = 0.001;
	public static final IRenderEnv BASIC_ENV = new RenderEnv(Dim.of(1280, 720), Rect.of(0, 75, 800, 450),
	        Dim.of(800, 600), false);

    private static final Logger LOG = LoggerFactory.getLogger(CoreTestUtil.class);

	private CoreTestUtil() {
	}

    public static Screen newScreen() {
        Dim vsize = BASIC_ENV.getVirtualSize();
        IScreenTextState textBoxState = new TestTextBoxState();
        return new Screen(Rect2D.of(0, 0, vsize.w, vsize.h), BASIC_ENV, textBoxState);
	}

	public static IScriptContext newScriptContext() {
	    return null;
	}

    public static ImageDrawable newImage() {
        ImageDrawable image = new ImageDrawable();
        image.setTexture(new TestTexture());
        return image;
	}

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

	public static void trySleep(long millis) {
	    try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
	}

    public static void assertEquals(Rect2D expected, Rect2D r) {
        assertEquals(expected.toArea2D(), r.toArea2D());
    }

    public static void assertEquals(Area2D expected, Area2D r) {
        assertEquals(expected.x, expected.y, expected.w, expected.h, r);
    }

    public static void assertEquals(double expectedX, double expectedY, double expectedW, double expectedH,
            Rect2D r) {

        assertEquals(expectedX, expectedY, expectedW, expectedH, r.toArea2D());
    }

    public static void assertEquals(double expectedX, double expectedY, double expectedW, double expectedH,
            Area2D r) {

        Assert.assertEquals("Invalid x: " + r, expectedX, r.x, EPSILON);
        Assert.assertEquals("Invalid y: " + r, expectedY, r.y, EPSILON);
        Assert.assertEquals("Invalid w: " + r, expectedW, r.w, EPSILON);
        Assert.assertEquals("Invalid h: " + r, expectedH, r.h, EPSILON);
    }

    public static void assertEquals(Insets2D expected, Insets2D actual) {
        Assert.assertEquals("Invalid top: " + actual, expected.top, actual.top, EPSILON);
        Assert.assertEquals("Invalid right: " + actual, expected.right, actual.right, EPSILON);
        Assert.assertEquals("Invalid bottom: " + actual, expected.bottom, actual.bottom, EPSILON);
        Assert.assertEquals("Invalid left: " + actual, expected.left, actual.left, EPSILON);
    }

    public static void assertEquals(double x, double y, Vec2 vec, double epsilon) {
        Assert.assertEquals(x, vec.x, epsilon);
        Assert.assertEquals(y, vec.y, epsilon);
    }

    public static <T> byte[] serializeObject(T obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        try {
            out.writeObject(obj);
        } finally {
            out.close();
        }
        return bout.toByteArray();
    }

    public static <T> T deserializeObject(byte[] data, Class<T> clazz)
            throws IOException, ClassNotFoundException {

        return deserializeObject(new ByteArrayInputStream(data), clazz);
    }

    public static <T> T deserializeObject(InputStream in, Class<T> clazz)
            throws IOException, ClassNotFoundException {

        ObjectInputStream oin = new ObjectInputStream(in);
        return clazz.cast(oin.readObject());
    }

}
