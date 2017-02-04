package nl.weeaboo.vn.impl.image.desc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageSubRect;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.impl.test.TestFileSystem;

public class ImageDefinitionIOTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private ImageDefinitionTestHelper testHelper;

    @Before
    public void before() {
        testHelper = new ImageDefinitionTestHelper();
    }

    /** Simple definition with no subrects */
    @Test
    public void minimal() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("minimal.json"));
        Assert.assertEquals("minimal", def.getFilename());
        Assert.assertEquals(Dim.of(16, 32), def.getSize());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMinifyFilter());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMagnifyFilter());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeX());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeY());
    }

    @Test
    public void allattrs() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("allattrs.json"));
        Assert.assertEquals("allattrs", def.getFilename());
        Assert.assertEquals(Dim.of(64, 128), def.getSize());
        Assert.assertEquals(GLScaleFilter.NEAREST, def.getMinifyFilter());
        Assert.assertEquals(GLScaleFilter.LINEAR, def.getMagnifyFilter());
        Assert.assertEquals(GLTilingMode.CLAMP, def.getTilingModeX());
        Assert.assertEquals(GLTilingMode.REPEAT, def.getTilingModeY());
    }

    @Test
    public void subrectMinimal() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("subrects1.json"));
        IImageSubRect subRect = Iterables.getOnlyElement(def.getSubRects());
        Assert.assertEquals("r1", subRect.getId());
        RectAssert.assertEquals(Area2D.of(1, 2, 3, 4), subRect.getArea().toArea2D(), EPSILON);
    }

    @Test
    public void testSerialize() throws IOException {
        // Load every valid file we have
        List<ImageDefinition> defs = Lists.newArrayList();
        defs.addAll(load("minimal.json"));
        defs.addAll(load("allattrs.json"));
        defs.addAll(load("subrects1.json"));

        String serialized = ImageDefinitionIO.serialize(defs);

        // Check that serialization doesn't change/lose anything
        Collection<ImageDefinition> actualDefs = ImageDefinitionIO.deserialize(serialized);
        for (ImageDefinition expected : defs) {
            ImageDefinition actual = testHelper.findDefById(actualDefs, expected.getFilename());
            testHelper.assertEquals(expected, actual);
        }
    }

    @Test
    public void fromFileSystem() throws IOException {
        MultiFileSystem fileSystem = TestFileSystem.newInstance();
        IWritableFileSystem wfs = fileSystem.getWritableFileSystem();
        writeDef(wfs, "img.json", "a");
        writeDef(wfs, "1/img.json", "b");
        writeDef(wfs, "1/2/img.json", "c");

        assertFileSystemContents(fileSystem, FilePath.empty(), ImmutableSet.of("a", "1/b", "1/2/c"));

    }

    private void assertFileSystemContents(IFileSystem fileSystem, FilePath rootFolder,
            ImmutableSet<String> expectedPaths) throws IOException {

        Set<String> actualPaths = Sets.newHashSet();
        for (FilePath path : ImageDefinitionIO.fromFileSystem(fileSystem, rootFolder).keySet()) {
            actualPaths.add(path.toString());
        }
        Assert.assertEquals(expectedPaths, actualPaths);
    }

    private void writeDef(IWritableFileSystem wfs, String path, String contents) throws IOException {
        ImageDefinition imageDef = testHelper.createImageDef(contents);
        String serialized = ImageDefinitionIO.serialize(ImmutableList.of(imageDef));
        FileSystemUtil.writeString(wfs, FilePath.of(path), serialized);
    }

    private Collection<ImageDefinition> load(String path) throws IOException {
        String content = Resources.toString(getClass().getResource("/imagedesc/" + path), Charsets.UTF_8);
        return ImageDefinitionIO.deserialize(content);
    }

}
