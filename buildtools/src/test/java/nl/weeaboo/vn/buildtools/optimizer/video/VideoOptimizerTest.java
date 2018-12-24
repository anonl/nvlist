package nl.weeaboo.vn.buildtools.optimizer.video;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.buildtools.optimizer.OptimizerTest;

public final class VideoOptimizerTest extends OptimizerTest {

    private VideoOptimizer optimizer;

    @Before
    public void before() throws IOException {
        extractResource("a.webm", "video/a.webm");

        optimizer = new VideoOptimizer(context);
    }

    /**
     * Run the optimizer using its default settings.
     */
    @Test
    public void testDefaultConfig() throws InterruptedException {
        optimizer.optimizeResources();

        assertOptimized("video/a.webm");
    }

}
