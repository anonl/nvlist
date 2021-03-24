package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import nl.weeaboo.io.FileUtil;

public final class ResourceOptimizerLauncherTest {

    private static final String CONFIG_FILE =
            "src/test/resources/nl/weeaboo/vn/buildtools/optimizer/optimizer-config-test.json";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private File outputF;

    @Before
    public void before() {
        outputF = new File(tempFolder.getRoot(), "output");
    }

    @Test
    public void testOptimize() throws IOException, InterruptedException {
        String[] args = { CONFIG_FILE, outputF.getAbsolutePath() };
        ResourceOptimizerLauncher.main(args);

        // An exception is thrown if the output folder isn't empty (to avoid data loss)
        outputF.mkdirs();
        FileUtil.writeUtf8(new File(outputF, "dummy.txt"), "dummy");
        Assert.assertThrows(IllegalStateException.class, () -> ResourceOptimizerLauncher.main(args));
    }
}
