package nl.weeaboo.vn.math;

import static nl.weeaboo.vn.LvnTestUtil.deserializeObject;
import static nl.weeaboo.vn.LvnTestUtil.serializeObject;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.LvnTestUtil;

public class VectorTest {

	private static final double E = LvnTestUtil.EPSILON;

	@Test
	public void vectorTest() {
		Vec2 a = new Vec2(1, 2);
		Vec2 b = new Vec2(4, 3);

		// Equality
        Assert.assertFalse(a.equals(null));
        System.out.println(a.toString());

		// Copy constructor
        LvnTestUtil.assertEquals(a.x, a.y, new Vec2(a), 0);
        Assert.assertEquals(a.hashCode(), new Vec2(a).hashCode());
        LvnTestUtil.assertEquals(a.x, a.y, a.clone(), 0);
        Assert.assertEquals(a.hashCode(), a.clone().hashCode());

		// Dot product
		Assert.assertEquals(10, a.dot(b), E);

		// Add, sub, scale
		a.add(b);
		LvnTestUtil.assertEquals(5, 5, a, E);
		a.sub(b);
		LvnTestUtil.assertEquals(1, 2, a, E);
		a.scale(-.5);
		LvnTestUtil.assertEquals(-.5, -1, a, E);
	}

	@Test
	public void length() {
        Vec2 a = new Vec2(1, 2);
        Vec2 b = new Vec2(4, 3);

        LvnTestUtil.assertEquals(a.y-b.y, b.x-a.x, a.cross(b), 0);

        Assert.assertEquals(25, b.lengthSquared(), E);
        Assert.assertEquals(5, b.length(), E);

        b.normalize();
        LvnTestUtil.assertEquals(4.0/5.0, 3.0/5.0, b, E);
	}

	@Test
	public void vectorSerializeTest() throws IOException, ClassNotFoundException {
		Vec2 a = new Vec2(1, 2);
        Assert.assertEquals(a, deserializeObject(serializeObject(a), Vec2.class));
		a = new Vec2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        Assert.assertEquals(a, deserializeObject(serializeObject(a), Vec2.class));
		a = new Vec2(Double.MIN_VALUE, Double.MIN_NORMAL);
        Assert.assertEquals(a, deserializeObject(serializeObject(a), Vec2.class));
		a = new Vec2(Double.NaN, 0.0);
        Assert.assertTrue(a.equals(deserializeObject(serializeObject(a), Vec2.class), E));
	}

}