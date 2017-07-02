package nl.weeaboo.vn.buildtools.project;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public final class ProjectFolderConfig {

    private final File projectFolder;
    private final File buildToolsFolder;

    public ProjectFolderConfig(File projectFolder, File buildToolsFolder) {
        this.projectFolder = Objects.requireNonNull(projectFolder);
        this.buildToolsFolder = Objects.requireNonNull(buildToolsFolder);
    }

    /**
     * The folder in which the /res and /build-res folders are stored. This is the root folder for the NVList
     * project.
     */
    public File getProjectFolder() {
        return projectFolder;
    }

    /**
     * The build-tools folder containing the Gradle build scripts and other engine-version-specific resources.
     * This folder may be a sub-folder of the project folder, or an entire separate folder.
     */
    public File getBuildToolsFolder() {
        return buildToolsFolder;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s[projectFolder=%s, buildToolsFolder=%s]",
                getClass().getSimpleName(), projectFolder, buildToolsFolder);
    }

}
