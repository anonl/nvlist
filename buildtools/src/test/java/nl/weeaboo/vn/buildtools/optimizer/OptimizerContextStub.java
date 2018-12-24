package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.TempFileProvider;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

public final class OptimizerContextStub implements IOptimizerContext {

    private final NvlistProjectConnection projectConnection;
    private final ITempFileProvider tempFileProvider;
    private final IParallelExecutor executor = new ParallelExecutorStub();
    private final ClassToInstanceMap<IOptimizerConfig> configStore;
    private final IOptimizerFileSet fileSet = new OptimizerFileSet();

    public OptimizerContextStub(ProjectFolderConfig projectFolders, File outputFolder) {
        projectConnection = NvlistProjectConnection.openProject(projectFolders);

        File tempFolder = new File(outputFolder, "temp");
        tempFolder.mkdirs();
        tempFileProvider = new TempFileProvider(tempFolder);

        configStore = MutableClassToInstanceMap.create();

        MainOptimizerConfig mainConfig = new MainOptimizerConfig(new File(outputFolder, "out"));
        configStore.put(MainOptimizerConfig.class, mainConfig);
    }

    @Override
    public void close() {
        projectConnection.close();
        tempFileProvider.deleteAll();
    }

    @Override
    public IParallelExecutor getExecutor() {
        return executor;
    }

    @Override
    public ITempFileProvider getTempFileProvider() {
        return tempFileProvider;
    }

    @Override
    public NvlistProjectConnection getProject() {
        return projectConnection;
    }

    @Override
    public MainOptimizerConfig getMainConfig() {
        MainOptimizerConfig config = configStore.getInstance(MainOptimizerConfig.class);
        Preconditions.checkState(config != null); // The ResourceOptimizerConfig should always be available
        return config;
    }

    @Override
    public <T extends IOptimizerConfig> T getConfig(Class<T> configClass, T defaultValue) {
        T result = configStore.getInstance(configClass);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public IOptimizerFileSet getFileSet() {
        return fileSet;
    }

}
