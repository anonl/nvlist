package nl.weeaboo.vn.buildtools.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates a new NVList project based on a project template (folder).
 */
public final class TemplateProjectGenerator implements IProjectGenerator {

    @Override
    public void createNewProject(Path targetFolder) throws IOException {
        // TODO: Implement

        Path resFolder = ProjectFolderConfig.getResFolder(targetFolder);
        createFolder(resFolder);

        Path buildResFolder = ProjectFolderConfig.getBuildResFolder(targetFolder);
        createFolder(buildResFolder);

        createFile(buildResFolder.resolve("build.properties"));
    }

    private void createFolder(Path folder) throws IOException {
        if (!Files.isDirectory(folder)) {
            Files.createDirectories(folder);
        }
    }

    private void createFile(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            Files.createFile(file);
        }
    }

}
