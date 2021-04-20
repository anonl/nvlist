package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.file.FilePathPattern;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageResizerConfig;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

/**
 * JSON representation of optimizer configuration.
 */
public final class OptimizerConfigJson {

    /** Screen resolutions formatted as width x height, e.g. "1280x720". */
    public final List<String> targetResolutions = new ArrayList<>();

    public OptimizerPreset preset = OptimizerPreset.LOSSLESS;

    /** File exclusion patterns, see {@link OptimizerFileSet#exclude(FilePathPattern)}. */
    public final List<String> exclude = new ArrayList<>();

    /**
     * Opens a connection to a NVList project using the settings from this config.
     */
    public NvlistProjectConnection openProject(Path projectFolder, Path buildToolsFolder) {
        return NvlistProjectConnection.openProject(new ProjectFolderConfig(projectFolder, buildToolsFolder));
    }

    /**
     * Initializes an optimizer context using the settings from this config.
     */
    public OptimizerContext createContext(NvlistProjectConnection projectConnection, File outputFolder) {
        // Main config
        MainOptimizerConfig mainConfig = new MainOptimizerConfig(outputFolder, preset);
        OptimizerContext context = new OptimizerContext(projectConnection, mainConfig);

        // Exclusion patterns
        for (String pattern : exclude) {
            context.getFileSet().exclude(FilePathPattern.fromGlob(pattern));
        }

        // Resizer config
        ImageResizerConfig resizerConfig = new ImageResizerConfig();
        for (String resolution : targetResolutions) {
            resizerConfig.addTargetResolution(parseResolution(resolution));
        }
        context.setConfig(resizerConfig);

        return context;
    }

    private static Dim parseResolution(String resolution) {
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)");
        Matcher matcher = pattern.matcher(resolution);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid resolution string: " + resolution);
        }

        try {
            int w = Integer.parseInt(matcher.group(1));
            int h = Integer.parseInt(matcher.group(2));
            return Dim.of(w, h);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid resolution string: " + resolution);
        }
    }

}
