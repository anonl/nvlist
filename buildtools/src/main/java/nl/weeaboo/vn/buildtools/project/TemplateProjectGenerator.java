package nl.weeaboo.vn.buildtools.project;

import java.io.File;

/**
 * Generates a new NVList project based on a project template (folder).
 */
final class TemplateProjectGenerator implements IProjectGenerator {

    @Override
    public void createNewProject(File targetFolder) {
        // TODO: Implement

        File resFolder = ProjectFolderConfig.getResFolder(targetFolder);
        resFolder.mkdirs();

        File buildResFolder = ProjectFolderConfig.getBuildResFolder(targetFolder);
        buildResFolder.mkdirs();
    }

}
