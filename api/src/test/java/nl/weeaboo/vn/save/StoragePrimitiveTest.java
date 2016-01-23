package nl.weeaboo.vn.save;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.ApiTestUtil;

public class StoragePrimitiveTest {

    @Test
    public void testNull() {
        StoragePrimitive p = StoragePrimitive.fromJson("null");
        assertBoolean(null, p);
        assertNumber(null, p);
        assertString(null, p);
    }

    @Test
    public void testBoolean() {
        StoragePrimitive p = StoragePrimitive.fromBoolean(true);
        assertBoolean(true, p);
        assertNumber(null, p);
        assertString("true", p);

        p = StoragePrimitive.fromBoolean(false);
        assertBoolean(false, p);
        assertNumber(null, p);
        assertString("false", p);
    }

    @Test
    public void testNumber() {
        // Integer
        StoragePrimitive p = StoragePrimitive.fromDouble(-678);
        assertBoolean(null, p);
        assertNumber(-678, p);
        assertString("-678", p);

        // Double
        p = StoragePrimitive.fromDouble(123.456);
        assertBoolean(null, p);
        assertNumber(123.456, p);
        assertString("123.456", p);
    }

    @Test
    public void testString() {
        StoragePrimitive p = StoragePrimitive.fromString("test string");
        assertBoolean(null, p);
        assertNumber(null, p);
        assertString("test string", p);
        // toString also returns the string value
        Assert.assertEquals("test string", p.toString());

        // Strings containing booleans are convertible to boolean
        assertBoolean(true, StoragePrimitive.fromString("true"));
        assertBoolean(false, StoragePrimitive.fromString("false"));

        // Strings containing numbers are convertible to double
        p = StoragePrimitive.fromString("1E3");
        assertBoolean(null, p);
        assertNumber(1000, p);
        assertString("1E3", p);
    }

    @Test
    public void testFromJson() {
        jsonConversionRoundtrip("null");
        jsonConversionRoundtrip("true");
        jsonConversionRoundtrip("false");
        jsonConversionRoundtrip("-123");
        jsonConversionRoundtrip("987.654");
        jsonConversionRoundtrip("\"test string\"");
        jsonConversionRoundtrip("\"escape \\n \\r \\t \\f \\\\ \\' \\\" chars\"");

        // Strings do not necessarily need to be quoted
        String unquoted = "for convenience, unquoted strings are also allowed";
        Assert.assertEquals("\"" + unquoted + "\"", StoragePrimitive.fromJson(unquoted).toJson());

        // Unquoted strings may contain escape sequences
        String unquotedEscapes = "\\n \\r \\t \\f \\\\ \\' \\\"";
        Assert.assertEquals("\"" + unquotedEscapes + "\"",
                StoragePrimitive.fromJson(unquotedEscapes).toJson());

        // undefined becomes null
        Assert.assertEquals(null, StoragePrimitive.fromJson("undefined"));
    }

    @Test
    public void invalidJson() {
        Assert.assertEquals(null, StoragePrimitive.fromJson(null));
    }

    @Test
    public void testEquals() {
        // Basic equality test
        StoragePrimitive alpha = StoragePrimitive.fromJson("\"test string\"");
        StoragePrimitive beta = StoragePrimitive.fromString("test string");
        Assert.assertEquals(alpha, beta);
        Assert.assertEquals(beta, alpha);
        Assert.assertNotEquals(alpha, null);
        Assert.assertNotEquals(alpha, StoragePrimitive.fromString(null));
        Assert.assertNotEquals(alpha, StoragePrimitive.fromDouble(123));

        /*
         * Values are not equal if internal representation is different, even if they have no observable
         * differences in behavior
         */
        StoragePrimitive strTrue = StoragePrimitive.fromString("true");
        StoragePrimitive boolTrue = StoragePrimitive.fromBoolean(true);
        Assert.assertNotEquals(strTrue, boolTrue);
        // Their hashCodes are also different
        Assert.assertNotEquals(strTrue.hashCode(), boolTrue.hashCode());

        // Make sure hashCode doesn't break if value is null
        Assert.assertEquals(0, StoragePrimitive.fromString(null).hashCode());
    }

    private static void jsonConversionRoundtrip(String json) {
        Assert.assertEquals(json, StoragePrimitive.fromJson(json).toJson());
    }

    private static void assertBoolean(Boolean expected, StoragePrimitive p) {
        if (expected != null) {
            // If we expect a non-null value, the default should be ignored
            Assert.assertEquals(expected, p.toBoolean(!expected));
        } else {
            // If we expect a null value, we always expect the supplied default
            Assert.assertEquals(false, p.toBoolean(false));
            Assert.assertEquals(true, p.toBoolean(true));
        }
    }

    private static void assertNumber(Number expected, StoragePrimitive p) {
        if (expected != null) {
            Assert.assertEquals(expected.doubleValue(), p.toDouble(-expected.doubleValue()),
                    ApiTestUtil.EPSILON);
        } else {
            Assert.assertEquals(123.0, p.toDouble(123.0), ApiTestUtil.EPSILON);
            Assert.assertEquals(456.0, p.toDouble(456.0), ApiTestUtil.EPSILON);
        }
    }

    private static void assertString(String expected, StoragePrimitive p) {
        Assert.assertEquals(expected, p.toString(null));
    }

}
