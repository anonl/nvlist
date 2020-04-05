package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
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

        File projectFolder = tempFolder.newFolder("project");
        File buildToolsFolder = tempFolder.newFolder("build-tools");
        folderConfig = new ProjectFolderConfig(projectFolder, buildToolsFolder);

        TemplateProjectGenerator projectGenerator = new TemplateProjectGenerator();
        projectGenerator.createNewProject(projectFolder);

        File outputFolder = tempFolder.newFolder("out");
        context = new OptimizerContextStub(folderConfig, outputFolder);
    }

    @After
    public final void baseAfter() {
        context.close();
    }

    protected void extractResource(String resourcePath, String targetPath) throws IOException {
        byte[] contents = Resources.toByteArray(getClass().getResource(resourcePath));
        LOG.debug("Extracting resource {} to {}", resourcePath, targetPath);
        writeResource(targetPath, contents);
    }

    protected void writeResource(String relPath, byte[] contents) throws IOException {
        File file = new File(folderConfig.getResFolder(), relPath.toString());
        file.getParentFile().mkdirs();
        Files.write(contents, file);
    }

    protected void assertOptimized(String... paths) {
        IOptimizerFileSet fileSet = context.getFileSet();
        for (String path : paths) {
            Assert.assertEquals("Path:" + path, true, fileSet.isOptimized(FilePath.of(path)));
        }
    }

}
