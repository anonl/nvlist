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
    public InputStream openInputStream(String path) throws IOException {
        for (IFileSystem fs : fileSystems) {
            if (fs.getFileExists(path)) {
                return fs.openInputStream(path);
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
                try {
                    fs.getFiles(out, path, recursive);
                } catch (FileNotFoundException fnfe) {
                    // Ignore and try the next file system
                }
			}
		}
	}

	@Override
	public void getSubFolders(Collection<String> out, String path, boolean recursive) throws IOException {
		for (IFileSystem fs : fileSystems) {
			if (fs.isOpen()) {
                try {
                    fs.getSubFolders(out, path, recursive);
                } catch (FileNotFoundException fnfe) {
                    // Ignore and try the next file system
                }
			}
		}
	}

    /**
     * @return The primary (first) writable file system in this multi filesystem, or {@code null} if no
     *         writable file system could be found.
     */
    public IWritableFileSystem getWritableFileSystem() {
        for (IFileSystem fs : fileSystems) {
            if (!fs.isReadOnly() && fs instanceof IWritableFileSystem) {
                return (IWritableFileSystem)fs;
            }
        }
        return null;
    }

}
