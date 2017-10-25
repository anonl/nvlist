package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public interface IResourceOptimizer {

    /**
     * Runs the resource optimizer.
     */
    void optimizeResources(NvlistProjectConnection sourceProject, ResourceOptimizerConfig config);

}
