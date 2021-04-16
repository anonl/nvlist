package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.io.FileUtil;
import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.OptimizerFileUtil;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerContext;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerFileSet;
import nl.weeaboo.vn.buildtools.optimizer.IParallelExecutor;
import nl.weeaboo.vn.buildtools.optimizer.MainOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.OptimizerPreset;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.IImageEncoder;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.JngEncoder;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.LosslessEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.PremultUtil;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.ResourceQualifiers;
import nl.weeaboo.vn.impl.core.SizeQualifier;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionCache;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionIO;

/**
 * Image resource optimizer.
 */
public final class ImageOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(ImageOptimizer.class);

    private final IParallelExecutor executor;
    private final MainOptimizerConfig optimizerConfig;
    private final ImageResizerConfig resizeConfig;
    private final NvlistProjectConnection project;
    private final IFileSystem resFileSystem;
    private final IOptimizerFileSet optimizerFileSet;
    private final ImageDefinitionCache imageDefCache;

    // --- State during optimization ---
    /** Definition per (optimized) image file */
    private Dim baseResolution;
    private final Map<FilePath, ImageDefinition> optimizedDefs = Maps.newHashMap();

    public ImageOptimizer(IOptimizerContext context) {
        executor = context.getExecutor();
        optimizerConfig = context.getMainConfig();
        resizeConfig = context.getConfig(ImageResizerConfig.class, new ImageResizerConfig());

        optimizerFileSet = context.getFileSet();

        project = context.getProject();
        resFileSystem = project.getResFileSystem();
        imageDefCache = new ImageDefinitionCache(resFileSystem);

        resetState();
    }

    private void resetState() {
        optimizedDefs.clear();

        baseResolution = Dim.of(project.getPref(NovelPrefs.WIDTH), project.getPref(NovelPrefs.HEIGHT));
    }

    /**
     * Runs the image optimizer.
     * @throws InterruptedException If the current thread is interrupted before the optimization is finished.
     */
    public void optimizeResources() throws InterruptedException {
        resetState();

        ImmutableList<FilePath> inputFiles = ImmutableList.copyOf(getImageFiles());
        for (Dim targetResolution : resizeConfig.getTargetResolutions(baseResolution)) {
            optimizeImages(inputFiles, targetResolution);
        }
    }

    private void optimizeImages(ImmutableList<FilePath> inputFiles, Dim targetResolution) throws InterruptedException {
        LOG.info("Optimizing images for target resolution: {}", targetResolution);

        optimizedDefs.clear();

        executor.invokeAndWait(inputFiles, inputFile -> {
            try {
                optimizeImage(inputFile, targetResolution);
            } catch (IOException | RuntimeException e) {
                LOG.warn("Error optimizing file: {}", inputFile, e);
            }
        });

        writeImageDefinitions();
    }

    private void writeImageDefinitions() {
        Multimap<FilePath, ImageDefinition> defsPerFolder = HashMultimap.create();
        for (Map.Entry<FilePath, ImageDefinition> entry : optimizedDefs.entrySet()) {
            defsPerFolder.put(entry.getKey().getParent(), entry.getValue());
        }

        for (Map.Entry<FilePath, Collection<ImageDefinition>> folderEntry : defsPerFolder.asMap().entrySet()) {
            FilePath jsonRelativePath = folderEntry.getKey().resolve(IImageDefinition.IMG_DEF_FILE);
            optimizerFileSet.markOptimized(jsonRelativePath);

            File outputF = new File(optimizerConfig.getOutputFolder(), jsonRelativePath.toString());

            String serialized = ImageDefinitionIO.serialize(folderEntry.getValue());
            try {
                FileUtil.writeUtf8(outputF, serialized);
            } catch (IOException e) {
                LOG.warn("Error writing {}: {}", jsonRelativePath, e);
            }
        }
    }

    private Iterable<FilePath> getImageFiles() {
        FileCollectOptions filter = FileCollectOptions.files(MediaType.IMAGE.getSubFolder());
        Iterable<FilePath> files = resFileSystem.getFiles(filter);
        files = OptimizerFileUtil.filterByExts(files, PixmapLoader.getSupportedImageExts());
        return files;
    }

    private void optimizeImage(FilePath inputFile, Dim targetResolution) throws IOException {
        if (!optimizerFileSet.requiresOptimize(inputFile)) {
            LOG.debug("Skip image: {}", inputFile);
            return;
        }

        LOG.debug("Optimizing image: {}", inputFile);

        ImageWithDef imageWithDef = load(inputFile);
        EncodedImage optimized;
        try {
            boolean resized = false;
            if (!baseResolution.equals(targetResolution)) {
                ImageResizer resizer = new ImageResizer(baseResolution, targetResolution);
                imageWithDef = resizer.process(imageWithDef);
                resized = true;
            }

            boolean premultiplyAlpha = premultiplyAlpha(imageWithDef.getPixmap());

            IImageEncoder imageEncoder = createEncoder();
            optimized = imageEncoder.encode(imageWithDef);
            if (!resized && !premultiplyAlpha) {
                IEncodedResource original = EncodedResource.fromFileSystem(resFileSystem, inputFile);

                // If the original image is usable and smaller, use it instead of our 'optimized' version
                if (original.getFileSize() < optimized.getFileSize()) {
                    optimized.dispose();
                    optimized = new EncodedImage(original, imageWithDef.getDef());
                }
            }

            // Give files with premultiplied alpha .pre.ext-style extension.
            if (premultiplyAlpha && optimized.hasAlpha()) {
                addPremultipyFileExt(optimized);
            }
        } finally {
            imageWithDef.dispose();
        }

        FilePath outputPath = getOutputPath(inputFile, targetResolution, optimized.getDef().getFilename());

        File outputF = new File(optimizerConfig.getOutputFolder(), outputPath.toString());
        Files.createParentDirs(outputF);
        Files.write(optimized.readBytes(), outputF);

        optimizedDefs.put(outputPath, optimized.getDef());
        optimizerFileSet.markOptimized(inputFile);
        optimized.dispose();
    }

    private ImageWithDef load(FilePath inputFile) throws IOException {
        Pixmap pixmap = PixmapLoader.load(resFileSystem, inputFile);

        IImageDefinition imageDef = imageDefCache.getMetaData(inputFile);
        if (imageDef == null) {
            imageDef = new ImageDefinition(inputFile.getName(), Dim.of(pixmap.getWidth(), pixmap.getHeight()));
        }

        return new ImageWithDef(pixmap, imageDef);
    }

    private boolean premultiplyAlpha(Pixmap pixmap) {
        if (!PixmapUtil.hasAlpha(pixmap.getFormat())) {
            return false;
        }
        PremultUtil.premultiplyAlpha(pixmap);
        return true;
    }

    private IImageEncoder createEncoder() {
        OptimizerPreset preset = optimizerConfig.getPreset();
        switch (preset) {
        case LOSSLESS:
            return new LosslessEncoder();
        case MEDIUM:
            return new JngEncoder();
        }
        throw new IllegalArgumentException("Unsupported preset: " + preset);
    }

    private void addPremultipyFileExt(EncodedImage encoded) {
        ImageDefinitionBuilder def = encoded.getDef().builder();
        String fn = def.getFilename();
        def.setFilename(Filenames.replaceExt(fn, "pre." + Filenames.getExtension(fn)));
        encoded.setDef(def.build());
    }

    private FilePath getOutputPath(FilePath inputPath, Dim targetResolution, String outputFilename) {
        FilePath folder = inputPath.getParent();
        if (!baseResolution.equals(targetResolution)) {
            folder = ResourceQualifiers.applyToRootFolder(folder, new SizeQualifier(targetResolution));
        }
        return folder.resolve(outputFilename);
    }

}
