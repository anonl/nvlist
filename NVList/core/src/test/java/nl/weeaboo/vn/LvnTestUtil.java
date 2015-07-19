package nl.weeaboo.vn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.math.Vec2;

public final class LvnTestUtil {

    public static final double EPSILON = 0.001;

    private LvnTestUtil() {
    }

    public static void assertEquals(Rect2D expected, Rect2D r) {
        assertEquals(expected.x, expected.y, expected.w, expected.h, r);
    }

    public static void assertEquals(double expectedX, double expectedY, double expectedW, double expectedH,
            Rect2D r) {

        Assert.assertEquals("Invalid: " + r, expectedX, r.x, EPSILON);
        Assert.assertEquals("Invalid: " + r, expectedY, r.y, EPSILON);
        Assert.assertEquals("Invalid: " + r, expectedW, r.w, EPSILON);
        Assert.assertEquals("Invalid: " + r, expectedH, r.h, EPSILON);
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