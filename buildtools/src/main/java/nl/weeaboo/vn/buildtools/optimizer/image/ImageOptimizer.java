package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.io.FileUtil;
import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerContext;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerFileSet;
import nl.weeaboo.vn.buildtools.optimizer.ResourceOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.IImageEncoder;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.JngEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.PremultUtil;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.image.ImageDefinitionCache;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionIO;

public final class ImageOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(ImageOptimizer.class);

    private final ResourceOptimizerConfig optimizerConfig;
    private final ImageResizerConfig resizeConfig;
    private final IFileSystem resFileSystem;
    private final IOptimizerFileSet optimizerFileSet;
    private final ImageDefinitionCache imageDefCache;

    // --- State during optimization ---
    /** Definition per (optimized) image file */
    private final Map<FilePath, ImageDefinition> optimizedDefs = Maps.newHashMap();


    public ImageOptimizer(IOptimizerContext context) {
        optimizerConfig = context.getConfig();
        resizeConfig = context.getConfig(ImageResizerConfig.class, new ImageResizerConfig());

        optimizerFileSet = context.getFileSet();

        NvlistProjectConnection project = context.getProject();
        resFileSystem = project.getResFileSystem();
        imageDefCache = new ImageDefinitionCache(resFileSystem);
    }

    private void resetState() {
        optimizedDefs.clear();
    }

    /**
     * Runs the image optimizer.
     */
    public void optimizeResources() {
        resetState();

        optimizeImages();
        writeImageDefinitions();
    }

    private void optimizeImages() {
        Iterable<FilePath> inputFiles;
        try {
            inputFiles = getImageFiles();
        } catch (IOException ioe) {
            LOG.warn("Unable to read folder", ioe);
            return;
        }

        for (FilePath inputFile : inputFiles) {
            try {
                optimizeImage(inputFile);
            } catch (IOException | RuntimeException e) {
                LOG.warn("Error optimizing file: {}", inputFile, e);
            }
        }
    }

    private void writeImageDefinitions() {
        Multimap<FilePath, ImageDefinition> defsPerFolder = HashMultimap.create();
        for (Entry<FilePath, ImageDefinition> entry : optimizedDefs.entrySet()) {
            defsPerFolder.put(entry.getKey().getParent(), entry.getValue());
        }

        for (Entry<FilePath, Collection<ImageDefinition>> folderEntry : defsPerFolder.asMap().entrySet()) {
            FilePath jsonRelativePath = folderEntry.getKey().resolve("img.json");
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

    private Iterable<FilePath> getImageFiles() throws IOException {
        FileCollectOptions filter = FileCollectOptions.files(MediaType.IMAGE.getSubFolder());
        return filterByExts(resFileSystem.getFiles(filter), PixmapLoader.getSupportedImageExts());
    }

    private void optimizeImage(FilePath inputFile) throws IOException {
        LOG.debug("Optimizing image: {}", inputFile);

        Pixmap pixmap = PixmapLoader.load(resFileSystem, inputFile);

        final boolean premultiplyAlpha = true && PixmapUtil.hasAlpha(pixmap.getFormat());
        if (premultiplyAlpha) {
            PremultUtil.premultiplyAlpha(pixmap);
        }

        IImageDefinition imageDef = imageDefCache.getImageDef(inputFile);
        if (imageDef == null) {
            imageDef = new ImageDefinition(inputFile.getName(), Dim.of(pixmap.getWidth(), pixmap.getHeight()));
        }

        ImageWithDef imageWithDef = new ImageWithDef(pixmap, imageDef);

        ImageResizer resizer = new ImageResizer(resizeConfig);
        ImageWithDef optimized = resizer.process(imageWithDef);
        pixmap.dispose();

        IImageEncoder imageEncoder = new JngEncoder();
        EncodedImage encoded = imageEncoder.encode(optimized);
        optimized.dispose();

        // Give files with premultiplied alpha .pre.ext-style extension.
        if (premultiplyAlpha && encoded.hasAlpha()) {
            addPremultipyFileExt(encoded);
        }
        FilePath outputPath = getOutputPath(inputFile, encoded.getDef().getFilename());

        File outputF = new File(optimizerConfig.getOutputFolder(), outputPath.toString());
        Files.createParentDirs(outputF);
        Files.write(encoded.readBytes(), outputF);

        optimizedDefs.put(outputPath, encoded.getDef());
        optimizerFileSet.markOptimized(inputFile);
    }

    private void addPremultipyFileExt(EncodedImage encoded) {
        ImageDefinitionBuilder def = encoded.getDef().builder();
        String fn = def.getFilename();
        def.setFilename(Filenames.replaceExt(fn, "pre." + Filenames.getExtension(fn)));
        encoded.setDef(def.build());
    }

    private FilePath getOutputPath(FilePath inputPath, String outputFilename) {
        FilePath folder = inputPath.getParent();
        return folder.resolve(outputFilename);
    }

    private static Iterable<FilePath> filterByExts(Iterable<FilePath> files, Collection<String> validExts) {
        // Use a tree set so we can match in a case-insensitive way
        TreeSet<String> validExtsSet = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        validExtsSet.addAll(validExts);

        return Iterables.filter(files, path -> validExtsSet.contains(path.getExt()));
    }

}
