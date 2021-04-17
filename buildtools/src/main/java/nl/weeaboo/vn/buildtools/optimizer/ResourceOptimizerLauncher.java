package nl.weeaboo.vn.buildtools.optimizer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.io.FileUtil;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.save.JsonUtil;

final class ResourceOptimizerLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceOptimizerLauncher.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        InitConfig.init();
        HeadlessGdx.init();

        Path projectFolder = Paths.get(args.length >= 1 ? args[0] : ".").toRealPath();

        Path buildToolsFolder = projectFolder.resolve("build-tools");
        if (args.length >= 2) {
            buildToolsFolder = Paths.get(args[1]).toRealPath();
        }

        Path configPath = ProjectFolderConfig.getBuildResFolder(projectFolder).resolve("optimizer.json");
        if (args.length >= 3) {
            configPath = Paths.get(args[2]).toRealPath();
        }

        Path outputPath = projectFolder.resolve("res-optimized");
        if (args.length >= 4) {
            outputPath = Paths.get(args[3]).toAbsolutePath();
        }

        LOG.info("Reading config: {}", configPath.toAbsolutePath());
        String json = FileUtil.readUtf8(configPath.toFile());
        OptimizerConfigJson config = JsonUtil.fromJson(OptimizerConfigJson.class, json);

        LOG.info("Output folder: {}", outputPath.toAbsolutePath());
        if (isNonEmptyFolder(outputPath)) {
            throw new IllegalStateException("Output folder isn't empty: " + outputPath);
        }

        try (NvlistProjectConnection connection = config.openProject(projectFolder, buildToolsFolder)) {
            try (OptimizerContext context = config.createContext(connection, outputPath.toFile())) {
                ResourceOptimizer optimizer = new ResourceOptimizer();
                optimizer.optimizeResources(context);
            }
        }

        LOG.info("Resource optimized finished. Output folder: {}", outputPath.toAbsolutePath());
    }

    private static boolean isNonEmptyFolder(Path outputPath) {
        String[] contents = outputPath.toFile().list();
        return contents != null && contents.length > 0;
    }

}
