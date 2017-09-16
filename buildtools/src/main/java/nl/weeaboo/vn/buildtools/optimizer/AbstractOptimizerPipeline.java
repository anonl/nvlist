package nl.weeaboo.vn.buildtools.optimizer;

/**
 * Base implementation of a resource optimizer pipeline.
 *
 * @see IOptimizerPipeline
 */
public abstract class AbstractOptimizerPipeline implements IOptimizerPipeline {

    protected final IOptimizerContext context;

    public AbstractOptimizerPipeline(IOptimizerContext context) {
        this.context = context;
    }

    /*
     * I don't know exactly how/where to abstract this, but I need the follwing logic:
     *
     * - For each file in the input, determine which pipeline to use. Note that for image/sound files, I need
     *   to add the contents of img.json/snd.json as well, so I guess I need mediatype-specific wrapper
     *   objects.
     * -- Files with no matching pipeline use an implicit default copy pipeline
     * -- Certain files are excluded from the copy pipeline (.svn, .git, img.json)
     * - Run the pipeline operations for each of the input files using a threadpool from the context
     * - For images/sounds, there are folder-wide img.json/snd.json files containing metadata. Those are
     *   problematic, since they can't be written by the single-file optimizers.
     *   There are multiple solutions for this:
     *   -- Write separate .json files for each resource, then combine them into a folder-wide one in a
     *      secondary post-processing step.
     *   -- Instead of just having a file as the final result of the pipeline, return a mediatype-specific
     *      object again. I guess this is actually the same as the original input (file+metadata). My current
     *      image optimizer uses Pixmap+metadata instead, but that can be changed so the first/last steps of
     *      the pipeline use File+metadata instead.
     */
}
