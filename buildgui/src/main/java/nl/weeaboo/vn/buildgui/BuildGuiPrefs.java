package nl.weeaboo.vn.buildgui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.save.JsonUtil;

final class BuildGuiPrefs {

    private static final Logger LOG = LoggerFactory.getLogger(BuildGuiPrefs.class);

    // If the prefs format changes, the key should change as well
    private static final String PREFS_KEY = "prefs001";

    private final Storage storage;

    private BuildGuiPrefs(Storage storage) {
        this.storage = storage;
    }

    public static BuildGuiPrefs load(String[] commandLineArgs) {
        // Load preferences from OS-dependent storage
        // Note: this may print a warning due to a JDK bug: https://bugs.openjdk.java.net/browse/JDK-8139507
        Preferences node = Preferences.userNodeForPackage(BuildGuiPrefs.class);
        String jsonString = node.get(PREFS_KEY, "{}");

        return load(jsonString, commandLineArgs);
    }

    private static BuildGuiPrefs load(String jsonString, String[] commandLineArgs) {
        // Load storage from JSON
        Storage storage = JsonUtil.fromJson(Storage.class, jsonString);

        // Override certain properties with command-line overrides
        if (commandLineArgs.length >= 1) {
            storage.projectPath = commandLineArgs[0];
        }
        if (commandLineArgs.length >= 2) {
            storage.buildToolsPath = commandLineArgs[1];
        }

        return new BuildGuiPrefs(storage);
    }

    private String toJson() {
        return JsonUtil.toJson(storage);
    }

    public void save() {
        String jsonString = toJson();

        LOG.info("Storing preferences: {}", jsonString);

        Preferences node = Preferences.userNodeForPackage(BuildGuiPrefs.class);
        node.put(PREFS_KEY, jsonString);
    }

    public ProjectFolderConfig getProjectFolderConfig() {
        Path projectFolder = Paths.get(storage.projectPath);
        Path buildToolsFolder = Paths.get(storage.buildToolsPath);
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
