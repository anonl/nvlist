package nl.weeaboo.vn.debug;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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

    private boolean cpuLoadError;

    public PerformanceMetrics() {
    }

    public String getPerformanceSummary() {
        List<String> lines = Lists.newArrayList();
        lines.add(String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()));
        lines.add(String.format("CPU: %s", getCpuLoadText()));
        lines.add(String.format("Memory use (heap): %s",
                StringUtil.formatMemoryAmount(Gdx.app.getJavaHeap())));
        lines.add(String.format("Memory use (non-heap): %s",
                StringUtil.formatMemoryAmount(Gdx.app.getNativeHeap())));
        return Joiner.on('\n').join(lines);
    }

    private String getCpuLoadText() {
        double cpuLoad = getCpuLoad();
        if (cpuLoad >= 0) {
            return String.format("%03d%%", Math.round(100 * cpuLoad));
        } else {
            return "---";
        }
    }

    /** @return The relative CPU load, or {@code -1} if not supported */
    public double getCpuLoad() {
        if (!cpuLoadError) {
            try {
                OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
                Method method = operatingSystem.getClass().getMethod("getProcessCpuLoad");
                method.setAccessible(true);
                return ((Number)method.invoke(operatingSystem)).doubleValue();
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

}
