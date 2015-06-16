package nl.weeaboo.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

public class MultiFileSystem implements IFileSystem {

	private final IFileSystem[] fileSystems;
	private boolean closed;
	
	public MultiFileSystem(IFileSystem... fileSystems) {
		this(Arrays.asList(fileSystems));
	}
	public MultiFileSystem(Collection<IFileSystem> fileSystems) {
		this.fileSystems = fileSystems.toArray(new IFileSystem[fileSystems.size()]);
	}
	
	@Override
	public void close() {
		closed = true;
		for (IFileSystem fs : fileSystems) {
			fs.close();
		}
	}

	@Override
	public boolean isOpen() {		
		return !closed;
	}

	@Override
	public final boolean isReadOnly() {
		return false;
	}

    @Override
    public InputStream newInputStream(String path) throws IOException {
        for (IFileSystem fs : fileSystems) {
            if (fs.getFileExists(path)) {
                return fs.newInputStream(path);
            }
        }
        throw new FileNotFoundException(path);
    }
    
	@Override
	public boolean getFileExists(String path) {
		for (IFileSystem fs : fileSystems) {
			if (fs.getFileExists(path)) {
			    return true;
			}
		}
		return false;
	}

	@Override
	public long getFileSize(String path) throws IOException {
		for (IFileSystem fs : fileSystems) {
			if (fs.getFileExists(path)) {
			    return fs.getFileSize(path);
			}
		}
		throw new FileNotFoundException(path);
	}

	@Override
	public long getFileModifiedTime(String path) throws IOException {
		for (IFileSystem fs : fileSystems) {
			if (fs.getFileExists(path)) {
			    return fs.getFileModifiedTime(path);
			}
		}
		throw new FileNotFoundException(path);
	}
	
	@Override
	public void getFiles(Collection<String> out, String path, boolean recursive) throws IOException {
		for (IFileSystem fs : fileSystems) {
			if (fs.isOpen()) {
			    fs.getFiles(out, path, recursive);
			}
		}
	}
				
	@Override
	public void getSubFolders(Collection<String> out, String path, boolean recursive) throws IOException {
		for (IFileSystem fs : fileSystems) {
			if (fs.isOpen()) {
			    fs.getSubFolders(out, path, recursive);
			}
		}
	}

}
