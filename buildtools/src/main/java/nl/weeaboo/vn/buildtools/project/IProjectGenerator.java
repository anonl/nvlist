package nl.weeaboo.vn.buildtools.project;

import java.io.File;

public interface IProjectGenerator {

    /**
     * Creates a new project in the specified target folder.
     */
    void createNewProject(File targetFolder);

}
