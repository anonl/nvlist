package nl.weeaboo.vn.buildgui;

import java.io.File;
import java.util.prefs.Preferences;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.save.JsonUtil;

final class BuildGuiPrefs {

    // If the prefs format changes, the key should change as well
    private static final String PREFS_KEY = "prefs001";

    private final Storage storage;

    private BuildGuiPrefs(Storage storage) {
        this.storage = storage;
    }

    public static BuildGuiPrefs load(String[] commandLineArgs) {
        // Load preferences from OS-dependent storage
        Preferences node = Preferences.userNodeForPackage(BuildGuiPrefs.class);

        // Load storage from JSON
        String jsonString = node.get(PREFS_KEY, "{}");
        Storage storage = JsonUtil.fromJson(Storage.class, jsonString);
        if (storage == null) {
            storage = new Storage();
        }

        // Override certain properties with command-line overrides
        if (commandLineArgs.length >= 1) {
            storage.projectPath = commandLineArgs[0];
        }
        if (commandLineArgs.length >= 2) {
            storage.buildToolsPath = commandLineArgs[1];
        }

        return new BuildGuiPrefs(storage);
    }

    public ProjectFolderConfig getProjectFolderConfig() {
        File projectFolder = new File(storage.projectPath);
        File buildToolsFolder = new File(storage.buildToolsPath);
        return new ProjectFolderConfig(projectFolder, buildToolsFolder);
    }

    public void setProjectFolderConfig(ProjectFolderConfig config) {
        storage.projectPath = config.getProjectFolder().toString();
        storage.buildToolsPath = config.getBuildToolsFolder().toString();
    }

    private static final class Storage {
        String projectPath = ".";
        String buildToolsPath = "build-tools";
    }

}
