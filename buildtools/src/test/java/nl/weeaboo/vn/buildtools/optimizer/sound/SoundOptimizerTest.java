package nl.weeaboo.vn.buildtools.optimizer.sound;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.buildtools.optimizer.OptimizerTest;

public final class SoundOptimizerTest extends OptimizerTest {

    private SoundOptimizer optimizer;

    @Before
    public void before() throws IOException {
        extractResource("a.ogg", "snd/a.ogg");

        optimizer = new SoundOptimizer(context);
    }

    /**
     * Run the optimizer using its default settings.
     */
    @Test
    public void testDefaultConfig() throws InterruptedException {
        optimizer.optimizeResources();

        assertOptimized("snd/a.ogg");
    }

}
