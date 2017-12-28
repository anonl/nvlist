package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

/**
 * This interface wraps all the services/helpers that are available to the various resource optimizers.
 */
public interface IOptimizerContext {

    /**
     * Returns a {@link ITempFileProvider} that manages creation and deletion of temporary files.
     */
    ITempFileProvider getTempFileProvider();

    NvlistProjectConnection getProject();

    ResourceOptimizerConfig getConfig();

    IOptimizerFileSet getFileSet();

}
