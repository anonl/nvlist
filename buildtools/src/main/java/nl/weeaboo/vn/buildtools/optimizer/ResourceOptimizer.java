package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.optimizer.image.ImageOptimizer;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundOptimizer;
import nl.weeaboo.vn.buildtools.optimizer.video.VideoOptimizer;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public final class ResourceOptimizer implements IResourceOptimizer {

    @Override
    public void optimizeResources(IOptimizerContext context) throws InterruptedException {
        ImageOptimizer imageOptimizer = new ImageOptimizer(context);
        imageOptimizer.optimizeResources();

        SoundOptimizer soundOptimizer = new SoundOptimizer(context);
        soundOptimizer.optimizeResources();

        VideoOptimizer videoOptimizer = new VideoOptimizer(context);
        videoOptimizer.optimizeResources();

        // Any files that don't have a specific optimizer are copied to the output folder
        NvlistProjectConnection project = context.getProject();
        ResourceOptimizerConfig config = context.getConfig();
        new UnoptimizedFileCopier().copyOtherResources(project.getResFileSystem(), context.getFileSet(),
                config.getOutputFolder());
    }

}
