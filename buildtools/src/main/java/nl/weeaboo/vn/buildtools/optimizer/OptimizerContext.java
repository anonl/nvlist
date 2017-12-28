package nl.weeaboo.vn.buildtools.optimizer;

import com.google.common.io.Files;

import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.TempFileProvider;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public final class OptimizerContext implements IOptimizerContext {

    private final NvlistProjectConnection projectConnection;
    private final ResourceOptimizerConfig config;

    private final ITempFileProvider tempFileProvider;
    private final IOptimizerFileSet fileSet;

    public OptimizerContext(NvlistProjectConnection projectConnection, ResourceOptimizerConfig config) {
        this.projectConnection = projectConnection;
        this.config = config;

        tempFileProvider = new TempFileProvider(Files.createTempDir());
        fileSet = new OptimizerFileSet();
    }

    @Override
    public NvlistProjectConnection getProject() {
        return projectConnection;
    }

    @Override
    public ResourceOptimizerConfig getConfig() {
        return config;
    }

    @Override
    public ITempFileProvider getTempFileProvider() {
        return tempFileProvider;
    }

    @Override
    public IOptimizerFileSet getFileSet() {
        return fileSet;
    }

}
