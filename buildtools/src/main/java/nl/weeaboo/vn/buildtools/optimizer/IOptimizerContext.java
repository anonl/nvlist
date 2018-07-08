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

    /**
     * Returns the {@link NvlistProjectConnection} which provides access to the internals, including resource files, of
     * a single NVList project.
     */
    NvlistProjectConnection getProject();

    /**
     * The main resource optimizer configuration.
     */
    ResourceOptimizerConfig getConfig();

    /**
     * Returns the configuration of the given type. If no configuration of that type was explicitly
     * registered, the supplied default instance is returned instead.
     */
    <T extends IOptimizerConfig> T getConfig(Class<T> configClass, T defaultValue);

    /**
     * The optimizer file set tracks which source files have already been processed.
     */
    IOptimizerFileSet getFileSet();

}
