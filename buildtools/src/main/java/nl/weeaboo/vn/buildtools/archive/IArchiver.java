package nl.weeaboo.vn.buildtools.archive;

import java.io.File;
import java.io.IOException;

import nl.weeaboo.filesystem.IFileSystem;

/**
 * Creates archive files containing multiple resoruces.
 */
public interface IArchiver {

    /**
     * Collects a folder filled with files into a single archive file.
     *
     * @param fileSystem A virtual filesystem containing the files and folders to add to the archive.
     * @param outputFile The output file to which the archive should be written.
     * @throws IOException If an I/O error occurs either while reading one of the input files, or while
     *         writing to the output file.
     */
    void archiveFiles(IFileSystem fileSystem, File outputFile) throws IOException;

}
