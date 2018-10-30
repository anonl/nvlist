package nl.weeaboo.vn.impl.debug;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.image.GdxTextureStore;

public final class PerformanceMetrics implements IPerformanceMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceMetrics.class);

    private double logicFps;

    private boolean cpuLoadError;
    private double cpuLoad;
    private long lastCpuLoadCheck;

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
        lines.add(String.format("Memory use (heap): %s",
                StringUtil.formatMemoryAmount(Gdx.app.getJavaHeap())));
        lines.add(StringUtil.formatRoot("Memory use (non-heap): %s",
                StringUtil.formatMemoryAmount(Gdx.app.getNativeHeap())));

        GdxTextureStore texStore = StaticEnvironment.TEXTURE_STORE.getIfPresent();
        if (texStore != null) {
            lines.add("Texture cache: " + texStore.getCacheStatus());
        }

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
        if (cpuLoadError) {
            return Double.NaN;
        }

        // Checking CPU load is slow, so rate limit the calls and return a cached result if called too often
        long now = System.nanoTime();
        if (now - lastCpuLoadCheck < 500_000_000L) {
            return cpuLoad;
        }

        lastCpuLoadCheck = now;
        try {
            // java.lang.management isn't supported on Android
            @SuppressWarnings("LiteralClassName")
            Class<?> managementFactory = Class.forName("java.lang.management.ManagementFactory");
            Object osBean = managementFactory.getMethod("getOperatingSystemMXBean").invoke(null);
            Method method = osBean.getClass().getMethod("getProcessCpuLoad");
            method.setAccessible(true);
            cpuLoad = ((Number)method.invoke(osBean)).doubleValue();
        } catch (Exception e) {
            LOG.info("Error obtaining CPU load (method not supported): " + e);
            setCpuLoadError();
        } catch (NoClassDefFoundError e) {
            LOG.info("Error obtaining CPU load: Required method not implemented on this platform");
            setCpuLoadError();
        }
        return cpuLoad;
    }

    private void setCpuLoadError() {
        cpuLoadError = true;
        cpuLoad = Double.NaN;
    }

    /** Internal 'game logic' update rate */
    public void setLogicFps(double logicFps) {
        this.logicFps = logicFps;
    }

}
