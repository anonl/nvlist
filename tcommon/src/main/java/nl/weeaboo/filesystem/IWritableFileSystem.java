package nl.weeaboo.filesystem;

import java.io.IOException;
import java.io.OutputStream;

public interface IWritableFileSystem extends IFileSystem {

    public void delete(String path) throws IOException;
    public void rename(String src, String dst) throws IOException;
    public void copy(String src, String dst) throws IOException;        
    
    public OutputStream openOutputStream(String path, boolean append) throws IOException;
    
}
