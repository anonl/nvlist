package nl.weeaboo.vn.buildtools.optimizer.video;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.OptimizerFileUtil;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerContext;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerFileSet;
import nl.weeaboo.vn.buildtools.optimizer.IParallelExecutor;
import nl.weeaboo.vn.buildtools.optimizer.MainOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.sound.encoder.FfmpegSoundEncoder;
import nl.weeaboo.vn.buildtools.optimizer.video.encoder.FfmpegVideoEncoder;
import nl.weeaboo.vn.buildtools.optimizer.video.encoder.IVideoEncoder;
import nl.weeaboo.vn.buildtools.optimizer.video.encoder.NoOpVideoEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.video.NativeVideoFactory;

/**
 * Video file optimizer.
 */
public final class VideoOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(VideoOptimizer.class);

    private final IParallelExecutor executor;
    private final MainOptimizerConfig optimizerConfig;
    private final IOptimizerFileSet optimizerFileSet;
    private final ITempFileProvider tempFileProvider;
    private final IFileSystem resFileSystem;

    // --- State during optimization ---
    private boolean ffmpegAvailable;


    public VideoOptimizer(IOptimizerContext context) {
        executor = context.getExecutor();
        optimizerConfig = context.getMainConfig();
        optimizerFileSet = context.getFileSet();
        tempFileProvider = context.getTempFileProvider();

        NvlistProjectConnection project = context.getProject();
        resFileSystem = project.getResFileSystem();
    }

    private void resetState() {
        ffmpegAvailable = FfmpegSoundEncoder.isAvailable();
    }

    /**
     * Runs the optimizer.
     *
     * @throws InterruptedException If the current thread is interrupted before the optimization is finished.
     */
    public void optimizeResources() throws InterruptedException {
        resetState();

        optimizeVideos();
    }

    private void optimizeVideos() throws InterruptedException {
        ImmutableList<FilePath> inputFiles;
        try {
            inputFiles = ImmutableList.copyOf(getVideoFiles());
        } catch (IOException ioe) {
            LOG.warn("Unable to read folder", ioe);
            return;
        }

        executor.invokeAndWait(inputFiles, inputFile -> {
            try {
                optimizeVideo(inputFile);
            } catch (IOException | RuntimeException e) {
                LOG.warn("Error optimizing audio file: {}", inputFile, e);
            }
        });
    }

    private Iterable<FilePath> getVideoFiles() throws IOException {
        FileCollectOptions filter = FileCollectOptions.files(MediaType.VIDEO.getSubFolder());
        return OptimizerFileUtil.filterByExts(resFileSystem.getFiles(filter),
                NativeVideoFactory.getSupportedFileExts());
    }

    private void optimizeVideo(FilePath inputFile) throws IOException {
        if (!optimizerFileSet.requiresOptimize(inputFile)) {
            LOG.debug("Skip video: {}", inputFile);
            return;
        }

        LOG.debug("Optimizing video: {}", inputFile);

        EncodedVideo input = loadInput(inputFile);

        IVideoEncoder encoder = createEncoder();
        // Note: encoded may point to the same resource as input
        EncodedVideo encoded = encoder.encode(input);

        FilePath outputPath = getOutputPath(inputFile, encoded.getFilename());

        // Write optimized file
        File outputF = new File(optimizerConfig.getOutputFolder(), outputPath.toString());
        Files.createParentDirs(outputF);
        Files.write(encoded.readBytes(), outputF);

        optimizerFileSet.markOptimized(inputFile);

        input.dispose();
        encoded.dispose();
    }

    private EncodedVideo loadInput(FilePath inputFile) {
        return new EncodedVideo(inputFile.getName(), EncodedResource.fromFileSystem(resFileSystem, inputFile));
    }

    private IVideoEncoder createEncoder() {
        if (ffmpegAvailable) {
            return new FfmpegVideoEncoder(tempFileProvider);
        } else {
            return new NoOpVideoEncoder();
        }
    }

    private FilePath getOutputPath(FilePath inputPath, String outputFilename) {
        FilePath folder = inputPath.getParent();
        return folder.resolve(outputFilename);
    }

}
