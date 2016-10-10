package nl.weeaboo.vn.image.desc;

import org.junit.Assert;
import org.junit.Test;

public class GLTilingModeTest {

    /** Each enum constant should map to a different constant */
    @Test
    public void glIdentifiersUnique() {
        long uniqueValues = GLTilingMode.VALUES.stream()
                .mapToInt(GLTilingMode::getGLIdentifier)
                .distinct()
                .count();
        Assert.assertEquals(uniqueValues, GLTilingMode.VALUES.size());
    }

    @Test
    public void fromString() {
        assertFromString(GLTilingMode.REPEAT, "repeat");
        assertFromString(GLTilingMode.CLAMP, "clamp");

        assertFromString(GLTilingMode.REPEAT, "true");
        assertFromString(GLTilingMode.CLAMP, "false");

        assertFromString(GLTilingMode.CLAMP, "");
    }

    @Test(expected = NullPointerException.class)
    public void fromNullString() {
        GLTilingMode.fromString(null);
    }

    private void assertFromString(GLTilingMode expected, String string) {
        Assert.assertEquals(expected, GLTilingMode.fromString(string));
    }

}
