package nl.weeaboo.vn.buildtools.file;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface ITempFileProvider {

    /**
     * Attempts to delete all temp files created by this class.
     */
    void deleteAll();

    /**
     * @return A temporary file.
     * @throws IOException If the temporary file couldn't be created.
     */
    File newTempFile() throws IOException;

}
