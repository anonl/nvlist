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
	
	private boolean readOnly;
	
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
			InMemoryFile file = getFile(path);
			file.delete();
			files.remove(path);
		}
	}

	@Override
	protected void copyImpl(String src, String dst) throws IOException {
		synchronized (files) {
			InMemoryFile file = getFile(src);
			files.put(dst, file.copy(dst));
		}
	}

	@Override
	protected InputStream newInputStreamImpl(String path) throws IOException {
		synchronized (files) {
			InMemoryFile file = getFile(path);
			return file.openInputStream();
		}
	}

	@Override
	protected OutputStream newOutputStreamImpl(String path, boolean append) throws IOException {
		synchronized (files) {
			InMemoryFile file = getFile(path);
			return file.openOutputStream();
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
			InMemoryFile file = getFile(path);
			return file.getFileSize();
		}
	}

	@Override
	protected long getFileModifiedTimeImpl(String path) throws IOException {
		synchronized (files) {
			InMemoryFile file = getFile(path);
			return file.getModifiedTime();
		}
	}

	protected InMemoryFile getFile(String path) throws FileNotFoundException {
		synchronized (files) {
			InMemoryFile file = files.get(path);
			if (file == null) {
				throw new FileNotFoundException(path);
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
				out.add(file.getName());
			}
		}
	}

}
