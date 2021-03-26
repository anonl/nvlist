package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.file.FilePathPattern;
import nl.weeaboo.vn.buildtools.optimizer.MainOptimizerConfig;
import nl.weeaboo.vn.buildtools.optimizer.OptimizerTest;

public final class ImageOptimizerTest extends OptimizerTest {

    @Before
    public void before() throws IOException {
        extractResource("a.png", "img/a.png");
        extractResource("b.png", "img/b.png");
        // Image with alpha
        extractResource("alphablend.png", "img/alphablend.png");
    }

    /**
     * Run the optimizer using its default settings.
     */
    @Test
    public void testDefaultConfig() throws InterruptedException {
        context.getFileSet().exclude(FilePathPattern.fromGlob("img/b.png"));

        ImageOptimizer optimizer = new ImageOptimizer(context);
        optimizer.optimizeResources();

        // img/b.png was marked as not needing to be optimized
        assertOptimized("img/a.png", "img/alphablend.png");
    }

    /**
     * Multi-resolution support
     */
    @Test
    public void testDifferentResolution() throws InterruptedException {
        ImageResizerConfig resizerConfig = new ImageResizerConfig();
        resizerConfig.addTargetResolution(Dim.of(128, 72));
        context.setConfig(ImageResizerConfig.class, resizerConfig);

        ImageOptimizer optimizer = new ImageOptimizer(context);
        optimizer.optimizeResources();

        assertOutputExists("img/a.jpg", "img-128x72/a.jpg");
    }

    /**
     * Errors writing/converting a single file shouldn't stop the optimzier.
     */
    @Test
    public void testWriteError() throws InterruptedException {
        MainOptimizerConfig mainConfig = context.getMainConfig();

        // Writing to this path will fail if a folder with the same name already exists
        new File(mainConfig.getOutputFolder(), "img/a.jpg").mkdirs();
        new File(mainConfig.getOutputFolder(), "img/img.json").mkdirs();

        // If converting an image failes, an error is logged and the optimized continues
        ImageOptimizer optimizer = new ImageOptimizer(context);
        optimizer.optimizeResources();
    }

}
