package nl.weeaboo.vn.impl.debug;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.test.StringTester;
import nl.weeaboo.vn.gdx.HeadlessGdx;

public final class PerformanceMetricsTest {

    private PerformanceMetrics metrics;

    @Before
    public void before() {
        HeadlessGdx.init();

        metrics = new PerformanceMetrics();
    }

    /**
     * The performance summary should at least say something about the frame rate and CPU/memory use.
     */
    @Test
    public void testPerformanceSummary() {
        new StringTester()
                .withSubString("FPS")
                .withSubString("CPU")
                .withSubString("Memory")
                .test(metrics.getPerformanceSummary());

        metrics.setLogicFps(12.3456789);
        StringTester.assertContains(metrics.getPerformanceSummary(), "FPS: 12.35 (logic)");
    }

    @Test
    public void testCpuLoadCaching() {
        Set<Double> valuesSeen = new HashSet<>();
        for (int n = 0; n < 1000; n++) {
            valuesSeen.add(metrics.getCpuLoad());
        }

        /*
         * Because checking the CPU load is an expensive operation, the implementation in PerformanceMetrics
         * must use caching to keep performance at an acceptable level.
         */
        Assert.assertTrue("Seen: " + valuesSeen.size(), valuesSeen.size() <= 2);
    }

}
