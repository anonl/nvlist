package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;

/**
 * Selects a resource folder based on a desired resolution.
 */
public final class ResolutionFolderSelector {

    private static final Logger LOG = LoggerFactory.getLogger(ResolutionFolderSelector.class);

    private final Dim vsize;
    private IFileSystem fileSystem;
    private FilePath basePath;

    public ResolutionFolderSelector(IEnvironment env, MediaType mediaType) {
        this(env.getRenderEnv().getVirtualSize(), env.getFileSystem(), mediaType.getSubFolder());
    }

    public ResolutionFolderSelector(Dim vsize, IFileSystem fileSystem, FilePath basePath) {
        this.vsize = vsize;
        this.fileSystem = fileSystem;

        // Strip trailing '/' (if any) because we need to create paths like img/, img-800x600/, etc.
        String path = basePath.toString();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        this.basePath = FilePath.of(path);
    }

    /**
     * Returns the resource folder that most closely matches the desired resolution.
     */
    public ResolutionPath select(Dim desiredResolution) {
        ResolutionPath best = new ResolutionPath(basePath, vsize);
        double bestScore = score(desiredResolution, best);
        try {
            for (ResolutionPath option : getOptions()) {
                double optionScore = score(desiredResolution, option);
                LOG.debug("Possible resolution: folder={}, score={}", option.folder, optionScore);

                if (optionScore > bestScore) {
                    best = option;
                    bestScore = optionScore;
                }
            }
        } catch (IOException ioe) {
            LOG.warn("Error scanning resource folders", ioe);
        }
        LOG.info("Best resolution: folder={}, score={}", best.folder, bestScore);
        return best;
    }

    private static double score(Dim desiredSize, ResolutionPath option) {
        double widthScore = -Math.abs(desiredSize.w - option.resolution.w);
        double heightScore = -Math.abs(desiredSize.h - option.resolution.h);
        return Math.min(widthScore, heightScore);
    }

    private List<ResolutionPath> getOptions() throws IOException {
        List<ResolutionPath> options = new ArrayList<>();
        FileCollectOptions collectOpts = FileCollectOptions.folders(basePath);
        collectOpts.recursive = false;
        for (FilePath folder : fileSystem.getFiles(collectOpts)) {
            ResolutionPath rp = tryParseResolution(folder);
            if (rp != null) {
                options.add(rp);
            }
        }
        return options;
    }

    private @Nullable ResolutionPath tryParseResolution(FilePath folder) {
        Pattern pattern = Pattern.compile(".*-(\\d+)x(\\d+)/?");
        Matcher matcher = pattern.matcher(folder.toString());
        if (!matcher.matches()) {
            return null;
        }

        try {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            Dim size = Dim.of(width, height);
            return new ResolutionPath(folder, size);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static final class ResolutionPath {

        public final FilePath folder;
        public final Dim resolution;

        public ResolutionPath(FilePath folder, Dim resolution) {
            this.folder = Checks.checkNotNull(folder);
            this.resolution = Checks.checkNotNull(resolution);
        }

    }
}
