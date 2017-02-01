package nl.weeaboo.vn.impl.sound.desc;

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

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinition;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionIO;
import nl.weeaboo.vn.impl.test.TestFileSystem;

public class SoundDefinitionIOTest {

    private SoundDefinitionTestHelper testHelper;

    @Before
    public void before() {
        testHelper = new SoundDefinitionTestHelper();
    }

    /** Simple definition with no subrects */
    @Test
    public void minimal() throws IOException {
        SoundDefinition def = Iterables.getOnlyElement(load("minimal.json"));
        Assert.assertEquals("minimal", def.getFilename().toString());
        Assert.assertEquals(null, def.getDisplayName());
    }

    @Test
    public void allattrs() throws IOException {
        SoundDefinition def = Iterables.getOnlyElement(load("allattrs.json"));
        Assert.assertEquals("allattrs", def.getFilename().toString());
        Assert.assertEquals("MyDisplayName", def.getDisplayName());
    }


    @Test
    public void testSerialize() throws IOException {
        // Load every valid file we have
        List<SoundDefinition> defs = Lists.newArrayList();
        defs.addAll(load("minimal.json"));
        defs.addAll(load("allattrs.json"));

        String serialized = SoundDefinitionIO.serialize(defs);

        // Check that serialization doesn't change/lose anything
        Collection<SoundDefinition> actualDefs = SoundDefinitionIO.deserialize(serialized);
        for (SoundDefinition expected : defs) {
            SoundDefinition actual = testHelper.findDefById(actualDefs, expected.getFilename());
            testHelper.assertEquals(expected, actual);
        }
    }

    @Test
    public void fromFileSystem() throws IOException {
        MultiFileSystem fileSystem = TestFileSystem.newInstance();
        IWritableFileSystem wfs = fileSystem.getWritableFileSystem();
        writeDef(wfs, "snd.json", "a");
        writeDef(wfs, "1/snd.json", "b");
        writeDef(wfs, "1/2/snd.json", "c");

        assertFileSystemContents(fileSystem, FilePath.empty(), ImmutableSet.of("a", "1/b", "1/2/c"));

    }

    private void assertFileSystemContents(IFileSystem fileSystem, FilePath rootFolder,
            ImmutableSet<String> expectedPaths) throws IOException {

        Set<String> actualPaths = Sets.newHashSet();
        for (FilePath path : SoundDefinitionIO.fromFileSystem(fileSystem, rootFolder).keySet()) {
            actualPaths.add(path.toString());
        }
        Assert.assertEquals(expectedPaths, actualPaths);
    }

    private void writeDef(IWritableFileSystem wfs, String path, String contents) throws IOException {
        SoundDefinition soundDef = testHelper.createSoundDef(contents);
        String serialized = SoundDefinitionIO.serialize(ImmutableList.of(soundDef));
        FileSystemUtil.writeString(wfs, FilePath.of(path), serialized);
    }

    private Collection<SoundDefinition> load(String path) throws IOException {
        String content = Resources.toString(getClass().getResource("/sounddesc/" + path), Charsets.UTF_8);
        return SoundDefinitionIO.deserialize(content);
    }

}
