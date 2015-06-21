package nl.weeaboo.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractFileSystem implements IFileSystem {

    private AtomicBoolean closed = new AtomicBoolean();

    public AbstractFileSystem() {
    }

    /**
     * @param path The user-specified unnormalized path.
     * @param allowNonExistant If {@code true}, allow paths to non-existant files.
     * @throws FileNotFoundException If {@code allowNonExistant} is false and the path doesn't point to a
     *         valid file.
     */
    protected String normalizePath(String path, boolean allowNonExistant) throws FileNotFoundException {
        return path;
    }

    @Override
    public final void close() {
        if (closed.compareAndSet(false, true)) {
            closeImpl();
        }
    }

    protected abstract void closeImpl();

    @Override
    public boolean isOpen() {
        return !closed.get();
    }

    @Override
    public final InputStream openInputStream(String path) throws IOException {
        return openInputStreamImpl(normalizePath(path, false));
    }

    protected abstract InputStream openInputStreamImpl(String path) throws IOException;
    
    @Override
    public final boolean getFileExists(String path) {
        try {
            return getFileExistsImpl(normalizePath(path, true));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    protected abstract boolean getFileExistsImpl(String path);

    @Override
    public final long getFileSize(String path) throws IOException {
        return getFileSizeImpl(normalizePath(path, false));
    }

    protected abstract long getFileSizeImpl(String path) throws IOException;

    @Override
    public final long getFileModifiedTime(String path) throws IOException {
        return getFileModifiedTimeImpl(normalizePath(path, false));
    }

    protected abstract long getFileModifiedTimeImpl(String path) throws IOException;

    @Override
    public void getFiles(Collection<String> out, String path, boolean recursive) throws IOException {
        FileCollectOptions opts = new FileCollectOptions();
        opts.recursive = recursive;
        opts.collectFolders = false;
        opts.collectFiles = true;

        getFiles(out, path, opts);
    }

    @Override
    public void getSubFolders(Collection<String> out, String path, boolean recursive) throws IOException {
        FileCollectOptions opts = new FileCollectOptions();
        opts.recursive = recursive;
        opts.collectFolders = true;
        opts.collectFiles = false;
        
        getFiles(out, path, opts);
    }

    protected abstract void getFiles(Collection<String> out, String prefix, FileCollectOptions opts) throws IOException;

}
