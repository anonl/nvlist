package nl.weeaboo.filesystem;

import java.io.File;
import java.io.IOException;

import nl.weeaboo.io.IRandomAccessFile;

public interface IFileArchive extends Iterable<ArchiveFileRecord> {

    void open(File f) throws IOException;
    void open(IRandomAccessFile f) throws IOException;
    
}
