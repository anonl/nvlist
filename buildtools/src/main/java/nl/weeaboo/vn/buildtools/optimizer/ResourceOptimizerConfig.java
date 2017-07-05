package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.util.Objects;

public final class ResourceOptimizerConfig {

    private final File outputFolder;

    public ResourceOptimizerConfig(File outputFolder) {
        this.outputFolder = Objects.requireNonNull(outputFolder);
    }

    public File getOutputFolder() {
        return outputFolder;
    }

}
