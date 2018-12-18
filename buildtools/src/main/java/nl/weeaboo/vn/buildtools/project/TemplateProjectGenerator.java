package nl.weeaboo.vn.buildtools.project;

import java.io.File;
import java.io.IOException;

/**
 * Generates a new NVList project based on a project template (folder).
 */
public final class TemplateProjectGenerator implements IProjectGenerator {

    @Override
    public void createNewProject(File targetFolder) throws IOException {
        // TODO: Implement

        File resFolder = ProjectFolderConfig.getResFolder(targetFolder);
        createFolder(resFolder);

        File buildResFolder = ProjectFolderConfig.getBuildResFolder(targetFolder);
        createFolder(buildResFolder);

        createFile(new File(buildResFolder, "build.properties"));
    }

    private void createFolder(File folder) throws IOException {
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IOException("Unable to create folder: " + folder);
        }
    }

    private void createFile(File file) throws IOException {
        if (!file.isFile() && !file.createNewFile()) {
            throw new IOException("Unable to create file: " + file);
        }
    }

}
