package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.project.TemplateProjectGenerator;

public abstract class OptimizerTest {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizerTest.class);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private ProjectFolderConfig folderConfig;

    protected OptimizerContextStub context;

    @Before
    public final void baseBefore() throws IOException {
        HeadlessGdx.init();

        Path projectFolder = tempFolder.newFolder("project").toPath();
        Path buildToolsFolder = tempFolder.newFolder("build-tools").toPath();
        folderConfig = new ProjectFolderConfig(projectFolder, buildToolsFolder);

        TemplateProjectGenerator projectGenerator = new TemplateProjectGenerator();
        projectGenerator.createNewProject(projectFolder);

        File outputFolder = tempFolder.newFolder("optimizer-out");
        context = new OptimizerContextStub(folderConfig, outputFolder);
    }

    @After
    public final void baseAfter() {
        context.close();
    }

    protected void extractResource(String resourcePath, String targetPath) throws IOException {
        byte[] contents = Resources.toByteArray(getClass().getResource(resourcePath));
        LOG.debug("Extracting resource {} to {}/{}", resourcePath, folderConfig.getResFolder(), targetPath);
        writeResource(targetPath, contents);
    }

    protected void writeResource(String relPath, byte[] contents) throws IOException {
        Path path = folderConfig.getResFolder().resolve(relPath);
        Files.createDirectories(path.getParent());
        Files.write(path, contents);
    }

    protected void assertOptimized(String... paths) {
        IOptimizerFileSet fileSet = context.getFileSet();
        for (String path : paths) {
            Assert.assertEquals("Path:" + path, true, fileSet.isOptimized(FilePath.of(path)));
        }
    }

    protected void assertOutputExists(String... paths) {
        File outF = context.getMainConfig().getOutputFolder();
        for (String path : paths) {
            Assert.assertEquals("Path:" + path, true, new File(outF, path).exists());
        }
    }
}
