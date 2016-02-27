package nl.weeaboo.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryFileSystem extends AbstractWritableFileSystem {

	private final Map<String, InMemoryFile> files = new HashMap<String, InMemoryFile>();

    private final boolean readOnly;

	public InMemoryFileSystem(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	protected void closeImpl() {
	}

	@Override
	protected void deleteImpl(String path) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(path, false);
			file.delete();
			files.remove(path);
		}
	}

	@Override
	protected void copyImpl(String src, String dst) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(src, false);
			files.put(dst, file.copy(dst));
		}
	}

	@Override
	protected InputStream openInputStreamImpl(String path) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(path, false);
			return file.openInputStream();
		}
	}

	@Override
	protected OutputStream newOutputStreamImpl(String path, boolean append) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(path, true);
            return file.openOutputStream(append);
		}
	}

	@Override
	protected boolean getFileExistsImpl(String path) {
		synchronized (files) {
			return files.containsKey(path);
		}
	}

	@Override
	protected long getFileSizeImpl(String path) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(path, false);
			return file.getFileSize();
		}
	}

	@Override
	protected long getFileModifiedTimeImpl(String path) throws IOException {
		synchronized (files) {
            InMemoryFile file = getFile(path, false);
			return file.getModifiedTime();
		}
	}

    protected InMemoryFile getFile(String path, boolean createIfNeeded) throws FileNotFoundException {
		synchronized (files) {
			InMemoryFile file = files.get(path);
			if (file == null) {
                if (!createIfNeeded) {
                    throw new FileNotFoundException(path);
                }
                file = new InMemoryFile(path);
                files.put(path, file);
			}
			return file;
		}
	}

	@Override
	protected void getFiles(Collection<String> out, String prefix, FileCollectOptions opts) throws IOException {
		if (!opts.collectFiles) {
			// Folders aren't supported
			return;
		}

		synchronized (files) {
			for (InMemoryFile file : files.values()) {
                if (file.getName().startsWith(prefix)) {
                    out.add(file.getName());
                }
			}
		}
	}

}
