package nl.weeaboo.vn.image.desc;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class GLScaleFilterTest {

    /** Each enum constant should map to a different constant */
    @Test
    public void glIdentifiersUnique() {
        long uniqueValues = GLScaleFilter.VALUES.stream()
                .mapToInt(GLScaleFilter::getGLIdentifier)
                .distinct()
                .count();
        Assert.assertEquals(uniqueValues, GLScaleFilter.VALUES.size());
    }

    @Test
    public void testMipmaps() {
        Set<GLScaleFilter> mipmaps = ImmutableSet.of(GLScaleFilter.NEAREST_MIPMAP, GLScaleFilter.LINEAR_MIPMAP);
        for (GLScaleFilter filter : GLScaleFilter.VALUES) {
            Assert.assertEquals(mipmaps.contains(filter), filter.isMipmap());
        }
    }

    @Test
    public void fromString() {
        assertFromString(GLScaleFilter.NEAREST, "nearest");
        assertFromString(GLScaleFilter.LINEAR, "linear");
        assertFromString(GLScaleFilter.NEAREST_MIPMAP, "nearest mipmap");
        assertFromString(GLScaleFilter.LINEAR_MIPMAP, "linear mipmap");

        assertFromString(GLScaleFilter.DEFAULT, "");
    }

    @Test(expected = NullPointerException.class)
    public void fromNullString() {
        GLScaleFilter.fromString(null);
    }

    private void assertFromString(GLScaleFilter expected, String string) {
        Assert.assertEquals(expected, GLScaleFilter.fromString(string));
    }

}
