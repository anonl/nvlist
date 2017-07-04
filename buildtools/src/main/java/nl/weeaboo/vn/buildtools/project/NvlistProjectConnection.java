package nl.weeaboo.vn.buildtools.project;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.RegularFileSystem;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.desktop.DesktopLauncher;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;

public final class NvlistProjectConnection implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistProjectConnection.class);

    private final ProjectFolderConfig folderConfig;

    private IFileSystem resFileSystem;
    private IWritableFileSystem outputFileSystem;
    private NovelPrefsStore preferences;

    private NvlistProjectConnection(ProjectFolderConfig folderConfig) {
        this.folderConfig = Objects.requireNonNull(folderConfig);
    }

    public static NvlistProjectConnection openProject(ProjectFolderConfig folderConfig) {
        NvlistProjectConnection projectModel = new NvlistProjectConnection(folderConfig);
        projectModel.open();
        return projectModel;
    }

    private void open() {
        File projectFolder = folderConfig.getProjectFolder();

        resFileSystem = DesktopLauncher.openResourceFileSystem(projectFolder);
        outputFileSystem = new RegularFileSystem(new File(projectFolder, "res/"));

        preferences = new NovelPrefsStore(resFileSystem, outputFileSystem);
        try {
            preferences.loadVariables();
        } catch (IOException e) {
            LOG.warn("Unable to load preferences", e);
        }
    }

    @Override
    public void close() {
        outputFileSystem.close();
        resFileSystem.close();
    }

    public ProjectFolderConfig getFolderConfig() {
        return folderConfig;
    }

    public <T> T getPref(Preference<T> preferenceDefinition) {
        return preferences.get(preferenceDefinition);
    }

    public IFileSystem getResFileSystem() {
        return resFileSystem;
    }

}
