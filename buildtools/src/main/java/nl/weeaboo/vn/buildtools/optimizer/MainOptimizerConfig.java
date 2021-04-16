package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.util.Objects;

/**
 * Global resource optimizer configuration not related to any particular resource type.
 */
public final class MainOptimizerConfig implements IOptimizerConfig {

    private final File outputFolder;
    private final OptimizerPreset preset;

    public MainOptimizerConfig(File outputFolder, OptimizerPreset preset) {
        this.outputFolder = Objects.requireNonNull(outputFolder);
        this.preset = Objects.requireNonNull(preset);
    }

    /**
     * The output folder to where the optimized resources should be written.
     */
    public File getOutputFolder() {
        return outputFolder;
    }

    /**
     * Preset which controls various quality/file-format settings.
     */
    public OptimizerPreset getPreset() {
        return preset;
    }

}
