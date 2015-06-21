package nl.weeaboo.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface IFileSystem {

    public void close();
    
	public InputStream openInputStream(String path) throws IOException;
	
	public boolean isOpen();
	public boolean isReadOnly();
	public boolean getFileExists(String path);
	public long getFileSize(String path) throws IOException;
	public long getFileModifiedTime(String path) throws IOException;
	
	public void getFiles(Collection<String> out, String path, boolean recursive) throws IOException;
	public void getSubFolders(Collection<String> out, String path, boolean recursive) throws IOException;
	
}
