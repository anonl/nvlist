package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.io.FileUtil;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.save.JsonUtil;

final class ResourceOptimizerLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceOptimizerLauncher.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        InitConfig.init();
        HeadlessGdx.init();

        File configPath = new File(args.length >= 1 ? args[0] : "optimizer.json");
        File outputPath = new File(args.length >= 2 ? args[1] : "res-optimized");

        LOG.info("Reading config: {}", configPath.getAbsolutePath());
        String json = FileUtil.readUtf8(configPath);
        OptimizerConfigJson config = JsonUtil.fromJson(OptimizerConfigJson.class, json);

        LOG.info("Output folder: {}", outputPath.getAbsolutePath());
        if (isNonEmptyFolder(outputPath)) {
            throw new IllegalStateException("Output folder isn't empty: " + outputPath);
        }

        try (NvlistProjectConnection connection = config.openProject()) {
            try (OptimizerContext context = config.createContext(connection, outputPath)) {
                ResourceOptimizer optimizer = new ResourceOptimizer();
                optimizer.optimizeResources(context);
            }
        }

        LOG.info("Resource optimized finished. Output folder: {}", outputPath.getAbsolutePath());
    }

    private static boolean isNonEmptyFolder(File outputPath) {
        String[] contents = outputPath.list();
        return contents != null && contents.length > 0;
    }

}
