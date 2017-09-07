package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;

public interface ITempFileProvider {

    /**
     * Attempts to delete all temp files created by this class.
     */
    void deleteAll();

    File newTempFile() throws IOException;

}
