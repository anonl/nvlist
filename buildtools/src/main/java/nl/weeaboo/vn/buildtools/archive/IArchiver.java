package nl.weeaboo.vn.buildtools.archive;

import java.io.File;
import java.io.IOException;

import nl.weeaboo.filesystem.IFileSystem;

public interface IArchiver {

    void archiveFiles(IFileSystem fileSystem, File outputFile) throws IOException;

}
