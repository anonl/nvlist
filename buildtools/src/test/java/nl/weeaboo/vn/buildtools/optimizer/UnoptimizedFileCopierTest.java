package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;

public final class UnoptimizedFileCopierTest extends OptimizerTest {

    private UnoptimizedFileCopier fileCopier;
    private File dstFolder;

    @Before
    public void before() throws IOException {
        dstFolder = tempFolder.newFolder("dst");

        extractResource("image/a.png", "img/a.png");
        extractResource("image/b.png", "img/b.png");

        IOptimizerFileSet fileSet = context.getFileSet();
        fileSet.markOptimized(FilePath.of("img/a.png"));

        fileCopier = new UnoptimizedFileCopier();
    }

    @Test
    public void testCopyOtherResources() {
        assertDstFileExists("img/a.png", false);
        assertDstFileExists("img/b.png", false);

        copyOtherResources();

        assertDstFileExists("img/a.png", false); // Not copied -- marked as optimized
        assertDstFileExists("img/b.png", true);

        // Attempt to copy again -- an internal error is logged because the output file already exists
        copyOtherResources();
        assertDstFileExists("img/b.png", true);
    }

    private void copyOtherResources() {
        fileCopier.copyOtherResources(context.getProject().getResFileSystem(), context.getFileSet(), dstFolder);
    }

    private void assertDstFileExists(String relPath, boolean expectExists) {
        Assert.assertEquals(expectExists, new File(dstFolder, relPath).exists());
    }

}
