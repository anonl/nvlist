package nl.weeaboo.vn.impl.debug;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import nl.weeaboo.common.StringUtil;

public final class PerformanceMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceMetrics.class);

    private double logicFps;
    private boolean cpuLoadError;

    public PerformanceMetrics() {
    }

    String getPerformanceSummary() {
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

    /**
     * @return The relative CPU load, or {@code -1} if not supported
     */
    public double getCpuLoad() {
        if (!cpuLoadError) {
            try {
                // java.lang.management isn't supported on Android
                @SuppressWarnings("LiteralClassName")
                Class<?> managementFactory = Class.forName("java.lang.management.ManagementFactory");
                Object osBean = managementFactory.getMethod("getOperatingSystemMXBean").invoke(null);
                Method method = osBean.getClass().getMethod("getProcessCpuLoad");
                method.setAccessible(true);
                return ((Number)method.invoke(osBean)).doubleValue();
            } catch (Exception e) {
                // Method not supported
                LOG.info("Error obtaining CPU load (method not supported): " + e);
                cpuLoadError = true;
            } catch (NoClassDefFoundError e) {
                LOG.info("Error obtaining CPU load: Required method not implemented on this platform");
                cpuLoadError = true;
            }
        }
        return -1;
    }

    /** Internal 'game logic' update rate */
    public void setLogicFps(double logicFps) {
        this.logicFps = logicFps;
    }

}
