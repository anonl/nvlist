package nl.weeaboo.vn.buildtools.project;

import java.io.File;

/**
 * Generates a new NVList project based on a project template (folder).
 */
final class TemplateProjectGenerator implements IProjectGenerator {

    @Override
    public void createNewProject(File targetFolder) {
        // TODO: Implement

        File resFolder = new File(targetFolder, "res");
        resFolder.mkdirs();

        File buildResFolder = new File(targetFolder, "build-res");
        buildResFolder.mkdirs();
    }

}
