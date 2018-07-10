package nl.weeaboo.vn.buildtools.optimizer;

public interface IResourceOptimizer {

    /**
     * Runs the resource optimizer.
     *
     * @throws InterruptedException If the current thread is interrupted before the optimization is finished.
     */
    void optimizeResources(IOptimizerContext context) throws InterruptedException;

}
