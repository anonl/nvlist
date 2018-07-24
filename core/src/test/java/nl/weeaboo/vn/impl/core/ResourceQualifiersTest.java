package nl.weeaboo.vn.impl.core;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public final class ResourceQualifiersTest {

    /**
     * Extracting qualifiers from a file path.
     */
    @Test
    public void testParse() {
        // No qualifiers
        assertFromPath("a/b");
        // Size in folder
        assertFromPath("a-1x2/b", size(1, 2));
        // Size in file
        assertFromPath("a/b-1x2", size(1, 2));
        // Same constraint in multiple segments
        assertFromPath("a-1x2/b-3x4", size(1, 2), size(3, 4));
    }

    /**
     * Find qualifiers by type.
     */
    @Test
    public void testFindQualifier() {
        // If the qualifier isn't found, null is returned instead
        assertFindQualifier("a/b", SizeQualifier.class, null);

        // Find returns the first matching qualifier
        assertFindQualifier("a-1x2/b-3x4", SizeQualifier.class, size(1, 2));
    }

    /**
     * Apply additional constraints to an existing path.
     */
    @Test
    public void testApplyConstraints() {
        // Simple path
        assertApplyConstraint("a/b", size(1, 2), "a-1x2/b");

        // Empty path
        assertApplyConstraint("", size(1, 2), "");

        // Already has a constraint
        assertApplyConstraint("a-1x2/b", size(3, 4), "a-1x2-3x4/b");
    }

    private void assertApplyConstraint(String pathString, IResourceQualifier qualifier, String expectedResult) {
        FilePath actual = ResourceQualifiers.applyToRootFolder(FilePath.of(pathString), qualifier);
        Assert.assertEquals(expectedResult, actual.toString());
    }

    private <T extends IResourceQualifier> void assertFindQualifier(String pathString, Class<T> type,
            @Nullable T expected) {

        ResourceQualifiers qualifiers = ResourceQualifiers.fromPath(FilePath.of(pathString));

        if (expected == null) {
            Assert.assertEquals(null, qualifiers.findQualifier(type));
        } else {
            Assert.assertEquals(expected.toPathString(), qualifiers.findQualifier(type).toPathString());
        }
    }

    private void assertFromPath(String pathString, IResourceQualifier... expectedQualifiers) {
        ResourceQualifiers qualifiers = ResourceQualifiers.fromPath(FilePath.of(pathString));

        int e = 0;
        for (IResourceQualifier actual : qualifiers) {
            Assert.assertEquals(expectedQualifiers[e].toPathString(), actual.toPathString());
            e++;
        }
    }

    private static SizeQualifier size(int w, int h) {
        return new SizeQualifier(Dim.of(w, h));
    }

}
