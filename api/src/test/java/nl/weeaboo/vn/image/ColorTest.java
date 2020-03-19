package nl.weeaboo.vn.image;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.SerializableTester;

public final class ColorTest {

    /** Make sure the color constants don't accidentally change between releases */
    @Test
    public void testConstants() {
        assertARGB(0xffffffff, Color.WHITE);
        assertARGB(0x00000000, Color.TRANSPARENT);
    }

    /** Check the implementation of {@link Color#hashCode()} and {@link Color#equals(Object)} */
    @Test
    public void testEquals() {
        new EqualsTester()
                .addEqualityGroup(Color.WHITE, Color.fromRGB(0xffffff), Color.fromRGBA(1., 1., 1., 1.))
                .addEqualityGroup(Color.TRANSPARENT, Color.fromRGBA(0., 0., 0., 0.))
                .addEqualityGroup(Color.fromRGBA(1., 1., 1., 0.))
                .testEquals();
    }

    @Test
    public void testSerialization() {
        Color original = Color.fromARGB(0x1234abcd);
        Color reserialized = SerializableTester.reserialize(original);

        Assert.assertNotSame(original, reserialized);
        Assert.assertEquals(original, reserialized);
    }

    @Test
    public void testFromRGB() {
        assertARGB(0xff123456, Color.fromRGB(0x123456));
        assertARGB(0xff4080bf, Color.fromRGB(.25, .50, .75));
    }

    @Test
    public void testFromARGB() {
        assertARGB(0x12345678, Color.fromARGB(0x12345678));
        assertARGB(0xbf004080, Color.fromRGBA(0, .25, .50, .75));
    }

    @Test
    public void testUnpremultiply() {
        Assert.assertEquals(Color.fromRGBA(.2, .4, .6, .5),
                Color.fromRGBA(.1, .2, .3, .5).unPremultiplied());
    }

    @Test
    public void testGetters() {
        // Colors are stored internally as doubles, so no precision is lost
        Color color = Color.fromRGBA(.111, .222, .333, .444);
        Assert.assertEquals(.111, color.getRed(), 0.0);
        Assert.assertEquals(.222, color.getGreen(), 0.0);
        Assert.assertEquals(.333, color.getBlue(), 0.0);
        Assert.assertEquals(.444, color.getAlpha(), 0.0);

        color = Color.fromARGB(0x10203040);
        Assert.assertEquals(0x10203040, color.getARGB());
        Assert.assertEquals(0x203040, color.getRGB());
    }

    private void assertARGB(int expectedARGB, Color actual) {
        Assert.assertEquals(Long.toHexString(expectedARGB), Long.toHexString(actual.getARGB()));
    }

}
