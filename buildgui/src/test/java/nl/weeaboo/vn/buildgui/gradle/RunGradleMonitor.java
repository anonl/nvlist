package nl.weeaboo.vn.buildgui.gradle;

import java.io.File;

import nl.weeaboo.vn.impl.InitConfig;

public final class RunGradleMonitor {

    /**
     * Tests {@link GradleMonitor} by connecting it to the NVList build.
     */
    public static void main(String[] args) throws CheckedGradleException {
        InitConfig.init();

        try (GradleMonitor monitor = new GradleMonitor()) {
            monitor.open(new File("..").getAbsoluteFile()); // NVList root project
        }
    }
}
