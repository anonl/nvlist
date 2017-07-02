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
import nl.weeaboo.vn.impl.core.NovelPrefsStore;

public final class ProjectModel {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectModel.class);

    private final ProjectFolderConfig folderConfig;

    private IWritableFileSystem resFileSystem;
    private NovelPrefsStore preferences;

    private ProjectModel(ProjectFolderConfig folderConfig) {
        this.folderConfig = Objects.requireNonNull(folderConfig);
    }

    public static ProjectModel createProjectModel(ProjectFolderConfig folderConfig) {
        ProjectModel projectModel = new ProjectModel(folderConfig);
        projectModel.init();
        return projectModel;
    }

    private void init() {
        File projectFolder = folderConfig.getProjectFolder();
        File resFolder = new File(projectFolder, "res");
        resFileSystem = new RegularFileSystem(resFolder);

        preferences = new NovelPrefsStore(resFileSystem, resFileSystem);
        try {
            preferences.loadVariables();
        } catch (IOException e) {
            LOG.warn("Unable to load preferences", e);
        }
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
