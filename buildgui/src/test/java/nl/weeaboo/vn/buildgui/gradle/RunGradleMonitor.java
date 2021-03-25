package nl.weeaboo.vn.buildgui.gradle;

import java.nio.file.Paths;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.InitConfig;

final class RunGradleMonitor {

    /**
     * Tests {@link GradleMonitor} by connecting it to the NVList build.
     * @throws CheckedGradleException If an error occurs while trying to connect to the Gradle build.
     */
    public static void main(String[] args) throws CheckedGradleException {
        InitConfig.init();

        // NVList root project
        ProjectFolderConfig folderConfig = new ProjectFolderConfig(Paths.get(".."), Paths.get(".."));

        try (GradleMonitor monitor = new GradleMonitor()) {
            monitor.open(folderConfig);
        }
    }
}
