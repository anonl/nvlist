package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.file.FilePathPattern;
import nl.weeaboo.vn.core.MediaType;

/**
 * Represents a pipeline of operations that may be performed on a folder of resource files.
 */
public interface IOptimizerPipeline {

    /**
     * The kind of files handled by this pipeline.
     */
    MediaType getMediaType();

    /**
     * The include pattern for files that should be handled by this pipeline. The include pattern is relative
     * to the root resource folder for resources of this pipeline's media type.
     */
    FilePathPattern getIncludePattern();

}
