package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageResizerConfig;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.InitConfig;

final class RunResourceOptimizer {

    /**
     * Test runner for the image optimizer pipeline.
     */
    public static void main(String[] args) throws InterruptedException {
        InitConfig.init();
        HeadlessGdx.init();

        File dstFolder = new File("tmp");

        // Clear destination folder first, so we don't get errors due to files already existing in the dst
        new FileHandle(dstFolder).deleteDirectory();

        // Recreate the destination folder
        dstFolder.mkdirs();

        // NVList root project
        ProjectFolderConfig folderConfig = new ProjectFolderConfig(new File("D:\\temp\\LoMa"), new File("."));
        try (NvlistProjectConnection connection = NvlistProjectConnection.openProject(folderConfig)) {
            ResourceOptimizerConfig config = new ResourceOptimizerConfig(dstFolder);

            try (OptimizerContext context = new OptimizerContext(connection, config)) {
                ImageResizerConfig resizerConfig = new ImageResizerConfig();
                resizerConfig.addTargetResolution(Dim.of(1280, 720));
                context.setConfig(resizerConfig);

                ResourceOptimizer optimizer = new ResourceOptimizer();
                optimizer.optimizeResources(context);
            }
        }
    }

}
