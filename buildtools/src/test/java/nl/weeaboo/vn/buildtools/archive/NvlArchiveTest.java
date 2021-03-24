package nl.weeaboo.vn.buildtools.archive;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.ZipFileArchive;

public final class NvlArchiveTest {

    private static final FilePath ONE = FilePath.of("a/1.txt");
    private static final FilePath TWO = FilePath.of("a/b/2.txt");

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InMemoryFileSystem fs = new InMemoryFileSystem(false);
    private final NvlArchiver archiver = new NvlArchiver();

    @Before
    public void before() throws IOException {
        FileSystemUtil.writeString(fs, ONE, "1");
        FileSystemUtil.writeString(fs, TWO, "2");
    }

    @Test
    public void testArchiveFiles() throws IOException {
        File outputF = new File(tempFolder.getRoot(), "out.nvl");
        archiver.archiveFiles(fs, outputF);

        ZipFileArchive arc = new ZipFileArchive();
        arc.open(outputF);
        try {
            Assert.assertEquals(ImmutableSet.of(ONE, TWO),
                    ImmutableSet.copyOf(arc.getFiles(FileCollectOptions.files(FilePath.empty()))));
        } finally {
            arc.close();
        }
    }

}
