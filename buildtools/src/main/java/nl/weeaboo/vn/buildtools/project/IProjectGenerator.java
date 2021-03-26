package nl.weeaboo.vn.buildtools.project;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Creates the folder structure for a new project.
 */
public interface IProjectGenerator {

    /**
     * Creates a new project in the specified target folder.
     *
     * @throws IOException If a fatal I/O error occurred while attempting to create the project on disk.
     */
    void createNewProject(Path targetFolder) throws IOException;

}
