package nl.weeaboo.vn.impl.debug;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.gdx.res.NativeMemoryTracker;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.image.GdxTextureStore;

/**
 * Default implementation of {@link IPerformanceMetrics}.
 */
public final class PerformanceMetrics implements IPerformanceMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceMetrics.class);

    private double logicFps;

    /**
     * Checking CPU load can be very slow depending on the JVM used, so rate limit the calls and return a
     * cached result if called too often.
     */
    private final RateLimiter cpuLoadRateLimiter = RateLimiter.create(.2);
    private double cpuLoad;

    public PerformanceMetrics() {
    }

    @Override
    public String getPerformanceSummary() {
        List<String> lines = Lists.newArrayList();
        lines.add(StringUtil.formatRoot("FPS: %d (render)", Gdx.graphics.getFramesPerSecond()));
        if (logicFps > 0) {
            lines.add(StringUtil.formatRoot("FPS: %.2f (logic)", logicFps));
        }
        lines.add(StringUtil.formatRoot("CPU: %s", getCpuLoadText()));
        lines.add(String.format("Memory (managed): %sM", Gdx.app.getJavaHeap() >> 20));

        GdxTextureStore texStore = StaticEnvironment.TEXTURE_STORE.getIfPresent();
        if (texStore != null) {
            lines.add("Memory (textures): " + texStore.getCacheStatus());
        }

        lines.add(String.format("Memory (other): %sM", NativeMemoryTracker.get().getTotalBytes() >> 20));

        return Joiner.on('\n').join(lines);
    }

    private String getCpuLoadText() {
        double cpuLoad = getCpuLoad();
        if (cpuLoad >= 0) {
            return StringUtil.formatRoot("%03d%%", Math.round(100 * cpuLoad));
        } else {
            return "---";
        }
    }

    @Override
    public double getCpuLoad() {
        if (!cpuLoadRateLimiter.tryAcquire()) {
            return cpuLoad;
        }

        try {
            Object osBean = getMXBean("OperatingSystemMXBean");
            Method method = osBean.getClass().getMethod("getProcessCpuLoad");
            method.setAccessible(true);
            cpuLoad = ((Number)method.invoke(osBean)).doubleValue();
        } catch (Exception e) {
            LOG.info("Error obtaining CPU load (method not supported): " + e);
            cpuLoadRateLimiter.setRate(1e-3);
            cpuLoad = Double.NaN;
        }
        return cpuLoad;
    }

    /** Internal 'game logic' update rate */
    public void setLogicFps(double logicFps) {
        this.logicFps = logicFps;
    }

    private static Object getMXBean(String beanName) throws Exception {
        // java.lang.management isn't supported on Android
        @SuppressWarnings("LiteralClassName")
        Class<?> managementFactory = Class.forName("java.lang.management.ManagementFactory");
        return managementFactory.getMethod("get" + beanName).invoke(null);
    }

}
