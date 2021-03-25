package nl.weeaboo.vn.buildtools.project;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

/**
 * Project and engine folders.
 */
public final class ProjectFolderConfig {

    private final Path projectFolder;
    private final Path buildToolsFolder;

    public ProjectFolderConfig() {
        this(Paths.get(""), Paths.get("build-tools"));
    }

    public ProjectFolderConfig(Path projectFolder, Path buildToolsFolder) {
        this.projectFolder = Objects.requireNonNull(projectFolder);
        this.buildToolsFolder = Objects.requireNonNull(buildToolsFolder);
    }

    /**
     * Returns a new instance, using a different project folder.
     */
    public ProjectFolderConfig withProjectFolder(Path newProjectFolder) {
        return new ProjectFolderConfig(newProjectFolder, buildToolsFolder);
    }

    /**
     * The folder in which the /res and /build-res folders are stored. This is the root folder for the NVList
     * project.
     */
    public Path getProjectFolder() {
        return projectFolder;
    }

    /**
     * Folder containing resources files used by the project.
     */
    public Path getResFolder() {
        return getResFolder(projectFolder);
    }

    /**
     * Folder containing resources files used by the project.
     */
    public static Path getResFolder(Path projectFolder) {
        return projectFolder.resolve("res");
    }

    /**
     * Folder containing resources and config for building the project.
     */
    public Path getBuildResFolder() {
        return getBuildResFolder(projectFolder);
    }

    /**
     * Folder containing resources and config for building the project.
     */
    public static Path getBuildResFolder(Path projectFolder) {
        return projectFolder.resolve("build-res");
    }

    /**
     * Properties file containing build properties.
     */
    public Path getBuildPropertiesFile() {
        return getBuildResFolder().resolve("build.properties");
    }

    /**
     * Generated build artifacts are stored in this folder.
     */
    public Path getBuildOutFolder() {
        return projectFolder.resolve("build-out");
    }

    /**
     * The build-tools folder containing the Gradle build scripts and other engine-version-specific resources.
     * This folder may be a sub-folder of the project folder, or an entire separate folder.
     */
    public Path getBuildToolsFolder() {
        return buildToolsFolder;
    }

    /**
     * Returns the canonical path for the given file. In some cases, no canonical path can be determined. In
     * those cases, the file's absolute path is returned instead.
     */
    public static String toCanonicalPath(Path file) {
        try {
            return file.toRealPath().toString();
        } catch (IOException ioe) {
            return file.toAbsolutePath().toString();
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s[projectFolder=%s, buildToolsFolder=%s]",
                getClass().getSimpleName(), projectFolder, buildToolsFolder);
    }

}
