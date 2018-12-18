package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.buildtools.optimizer.OptimizerTest;

public final class ImageOptimizerTest extends OptimizerTest {

    private ImageOptimizer optimizer;

    @Before
    public void before() throws IOException {
        extractResource("a.png", "img/a.png");
        extractResource("b.png", "img/b.png");

        optimizer = new ImageOptimizer(context);
    }

    /**
     * Run the optimizer using its default settings.
     */
    @Test
    public void testDefaultConfig() throws InterruptedException {
        optimizer.optimizeResources();

        assertOptimized("img/a.png", "img/b.png");
    }

}
