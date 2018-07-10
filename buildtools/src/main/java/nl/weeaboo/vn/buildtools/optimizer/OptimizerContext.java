package nl.weeaboo.vn.buildtools.optimizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.io.Files;
import com.google.common.util.concurrent.MoreExecutors;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.TempFileProvider;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public final class OptimizerContext implements IOptimizerContext {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizerContext.class);

    private final NvlistProjectConnection projectConnection;

    private final ITempFileProvider tempFileProvider;
    private final IOptimizerFileSet fileSet;
    private final ExecutorService executorService;
    private final IParallelExecutor parallelExecutor;
    private final ClassToInstanceMap<IOptimizerConfig> configStore;

    public OptimizerContext(NvlistProjectConnection projectConnection, ResourceOptimizerConfig mainConfig) {
        this.projectConnection = projectConnection;

        tempFileProvider = new TempFileProvider(Files.createTempDir());
        fileSet = new OptimizerFileSet();

        executorService = ParallelExecutor.newExecutorService();
        parallelExecutor = new ParallelExecutor(executorService);

        configStore = MutableClassToInstanceMap.create();
        configStore.put(ResourceOptimizerConfig.class, mainConfig);
    }

    @Override
    public void close() {
        if (!MoreExecutors.shutdownAndAwaitTermination(executorService, 10, TimeUnit.SECONDS)) {
            LOG.error("Timeout while waiting for executor to shut down");
        }
        tempFileProvider.deleteAll();
    }

    @Override
    public NvlistProjectConnection getProject() {
        return projectConnection;
    }

    @Override
    public ResourceOptimizerConfig getConfig() {
        ResourceOptimizerConfig config = configStore.getInstance(ResourceOptimizerConfig.class);
        Preconditions.checkState(config != null); // The ResourceOptimizerConfig should always be available
        return config;
    }

    @Override
    public <T extends IOptimizerConfig> T getConfig(Class<T> configClass, T defaultInstance) {
        T result = configStore.getInstance(configClass);
        if (result == null) {
            result = defaultInstance;
        }
        return result;
    }

    /**
     * Registers a configuration holder.
     * @see #getConfig(Class, IOptimizerConfig)
     */
    public <T extends IOptimizerConfig> void setConfig(T instance) {
        Checks.checkNotNull(instance);

        configStore.put(instance.getClass(), instance);
    }

    @Override
    public ITempFileProvider getTempFileProvider() {
        return tempFileProvider;
    }

    @Override
    public IOptimizerFileSet getFileSet() {
        return fileSet;
    }

    @Override
    public IParallelExecutor getExecutor() {
        return parallelExecutor;
    }

}
