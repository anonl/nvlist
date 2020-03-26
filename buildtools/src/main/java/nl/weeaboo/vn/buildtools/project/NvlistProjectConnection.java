package nl.weeaboo.vn.buildtools.project;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.RegularFileSystem;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;

/**
 * Represents a NVList project.
 */
public final class NvlistProjectConnection implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistProjectConnection.class);

    private final ProjectFolderConfig folderConfig;

    private IFileSystem resFileSystem;
    private IWritableFileSystem outputFileSystem;
    private NovelPrefsStore preferences;
    private BuildProperties buildProperties;

    private NvlistProjectConnection(ProjectFolderConfig folderConfig) {
        this.folderConfig = Objects.requireNonNull(folderConfig);
    }

    /**
     * Opens a connection to the project stored in the specified folder.
     */
    public static NvlistProjectConnection openProject(ProjectFolderConfig folderConfig) {
        NvlistProjectConnection projectModel = new NvlistProjectConnection(folderConfig);
        projectModel.open();
        return projectModel;
    }

    private void open() {
        resFileSystem = new DesktopGdxFileSystem(folderConfig.getResFolder());
        outputFileSystem = new RegularFileSystem(folderConfig.getResFolder());

        preferences = new NovelPrefsStore(resFileSystem, outputFileSystem);
        try {
            preferences.loadVariables();
        } catch (IOException e) {
            LOG.warn("Unable to load preferences", e);
        }

        try {
            buildProperties = BuildProperties.fromFile(folderConfig.getBuildPropertiesFile());
        } catch (IOException e) {
            LOG.warn("Unable to load {}", folderConfig.getBuildPropertiesFile(), e);
        }
    }

    @Override
    public void close() {
        outputFileSystem.close();
        resFileSystem.close();
    }

    /**
     * @return An object containing the project folder and related configuration.
     */
    public ProjectFolderConfig getFolderConfig() {
        return folderConfig;
    }

    /**
     * Returns a configurable preference value from the project.
     * @see NovelPrefs
     */
    public <T> T getPref(Preference<T> preferenceDefinition) {
        return preferences.get(preferenceDefinition);
    }

    /**
     * Returns a property from the build-properties file.
     * @see ProjectFolderConfig#getBuildPropertiesFile()
     */
    public String getBuildProperty(String key, String defaultValue) {
        return buildProperties.getProperty(key, defaultValue);
    }

    /**
     * A virtual file system providing read-only access to the project's resources.
     */
    public IFileSystem getResFileSystem() {
        return resFileSystem;
    }

}
