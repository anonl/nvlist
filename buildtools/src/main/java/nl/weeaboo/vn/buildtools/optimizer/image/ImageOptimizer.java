package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.io.Files;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildtools.optimizer.ResourceOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.JngEncoder;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.image.ImageDefinitionCache;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public final class ImageOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(ImageOptimizer.class);

    private final ResourceOptimizerConfig config;
    private final IFileSystem resFileSystem;
    private final ImageDefinitionCache imageDefCache;

    public ImageOptimizer(NvlistProjectConnection project, ResourceOptimizerConfig config) {
        this.config = config;

        resFileSystem = project.getResFileSystem();
        imageDefCache = new ImageDefinitionCache(resFileSystem);
    }

    public void optimizeResources() {
        Iterable<FilePath> inputFiles;
        try {
            inputFiles = resFileSystem.getFiles(getResourceFileFilter());
        } catch (IOException ioe) {
            LOG.warn("Unable to read folder", ioe);
            return;
        }

        for (FilePath inputFile : inputFiles) {
            // TODO Only try to convert supported file types. Perhaps define ImageResourceLoader's auto file exts in MediaType.
            try {
                optimizeImage(inputFile);
            } catch (IOException | RuntimeException e) {
                LOG.warn("Error optimizing file: {}", inputFile, e);
            }
        }
    }

    private void optimizeImage(FilePath inputFile) throws IOException {
        LOG.debug("Optimizing image: {}", inputFile);

        File dstF = config.getOutputFolder();

        Pixmap pixmap = PixmapLoader.load(resFileSystem, inputFile);
        IImageDefinition imageDef = imageDefCache.getImageDef(inputFile);
        if (imageDef == null) {
            imageDef = new ImageDefinition(inputFile.getName(), Dim.of(pixmap.getWidth(), pixmap.getHeight()));
        }

        ImageWithDef imageWithDef = new ImageWithDef(pixmap, imageDef);

        ImageResizerConfig resizeConfig = new ImageResizerConfig();
        resizeConfig.setScaleFactor(0.5);
        ImageResizer resizer = new ImageResizer(resizeConfig);
        ImageWithDef optimized = resizer.process(imageWithDef);

        JngEncoder jngEncoder = new JngEncoder();
        EncodedImage encoded = jngEncoder.encode(optimized);

        File outputF = new File(dstF, inputFile.withExt("jng").toString());
        Files.createParentDirs(outputF);
        Files.write(encoded.readBytes(), outputF);
    }

    protected FileCollectOptions getResourceFileFilter() {
        return FileCollectOptions.files(MediaType.IMAGE.getSubFolder());
    }

}
