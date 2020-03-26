package nl.weeaboo.vn.buildtools.project;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Project and engine folders.
 */
public final class ProjectFolderConfig {

    private final File projectFolder;
    private final File buildToolsFolder;

    public ProjectFolderConfig() {
        this(new File(""), new File("build-tools"));
    }

    public ProjectFolderConfig(File projectFolder, File buildToolsFolder) {
        this.projectFolder = Objects.requireNonNull(projectFolder);
        this.buildToolsFolder = Objects.requireNonNull(buildToolsFolder);
    }

    /**
     * Returns a new instance, using a different project folder.
     */
    public ProjectFolderConfig withProjectFolder(File newProjectFolder) {
        return new ProjectFolderConfig(newProjectFolder, buildToolsFolder);
    }

    /**
     * The folder in which the /res and /build-res folders are stored. This is the root folder for the NVList
     * project.
     */
    public File getProjectFolder() {
        return projectFolder;
    }

    /**
     * Folder containing resources files used by the project.
     */
    public File getResFolder() {
        return getResFolder(projectFolder);
    }

    /**
     * Folder containing resources files used by the project.
     */
    public static File getResFolder(File projectFolder) {
        return new File(projectFolder, "res");
    }

    /**
     * Folder containing resources and config for building the project.
     */
    public File getBuildResFolder() {
        return getBuildResFolder(projectFolder);
    }

    /**
     * Folder containing resources and config for building the project.
     */
    public static File getBuildResFolder(File projectFolder) {
        return new File(projectFolder, "build-res");
    }

    /**
     * Properties file containing build properties.
     */
    public File getBuildPropertiesFile() {
        return new File(getBuildResFolder(), "build.properties");
    }

    /**
     * The build-tools folder containing the Gradle build scripts and other engine-version-specific resources.
     * This folder may be a sub-folder of the project folder, or an entire separate folder.
     */
    public File getBuildToolsFolder() {
        return buildToolsFolder;
    }

    /**
     * Returns the canonical path for the given file. In some cases, no canonical path can be determined. In
     * those cases, the file's absolute path is returned instead.
     */
    public static String toCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ioe) {
            return file.getAbsolutePath();
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s[projectFolder=%s, buildToolsFolder=%s]",
                getClass().getSimpleName(), projectFolder, buildToolsFolder);
    }

}
