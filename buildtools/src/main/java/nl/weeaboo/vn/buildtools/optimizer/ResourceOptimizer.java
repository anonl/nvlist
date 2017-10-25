package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.optimizer.image.ImageOptimizer;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public final class ResourceOptimizer implements IResourceOptimizer {

    @Override
    public void optimizeResources(NvlistProjectConnection project, ResourceOptimizerConfig config) {
        // TODO: Implement
        // ImageOptimizer imageOptimizer = new ImageOptimizer(sourceProject, config);

        ImageOptimizer imageOptimizer = new ImageOptimizer(project, config);
        imageOptimizer.optimizeResources();

        // TODO: Ensure the full contents of the resource filesystem is copied to the outputFolder, even files that aren't optimized.
    }

}
