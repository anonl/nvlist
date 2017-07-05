package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public interface IResourceOptimizer {

    void optimizeResources(NvlistProjectConnection sourceProject, ResourceOptimizerConfig config);

}
