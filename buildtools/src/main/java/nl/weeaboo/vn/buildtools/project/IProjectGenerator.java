package nl.weeaboo.vn.buildtools.project;

import java.io.File;
import java.io.IOException;

public interface IProjectGenerator {

    /**
     * Creates a new project in the specified target folder.
     *
     * @throws IOException If a fatal I/O error occurred while attempting to create the project on disk.
     */
    void createNewProject(File targetFolder) throws IOException;

}
