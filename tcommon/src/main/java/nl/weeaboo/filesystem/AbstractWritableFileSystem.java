package nl.weeaboo.filesystem;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractWritableFileSystem extends AbstractFileSystem implements IWritableFileSystem {

    @Override
    public boolean isReadOnly() {
        return false;
    }

    protected void checkWritable() throws IOException {
        if (isReadOnly()) {
            throw new IOException("FileSystem is read-only");
        }
        if (!isOpen()) {
            throw new IOException("FileSystem is closed");
        }
    }
    
    @Override
    public final OutputStream openOutputStream(String path, boolean append) throws IOException {
        checkWritable();
        return newOutputStreamImpl(normalizePath(path, true), append);
    }
    
    protected abstract OutputStream newOutputStreamImpl(String path, boolean append) throws IOException;
    
    @Override
    public final void delete(String path) throws IOException {
        checkWritable();
        deleteImpl(normalizePath(path, true));
    }
    
    protected abstract void deleteImpl(String path) throws IOException;
    
    @Override
    public final void rename(String src, String dst) throws IOException {
        checkWritable();
        renameImpl(normalizePath(src, false), normalizePath(dst, true));
    }
    
    protected void renameImpl(String src, String dst) throws IOException {
        copyImpl(src, dst);
        deleteImpl(src);
    }
    
    @Override
    public final void copy(String src, String dst) throws IOException {
        checkWritable();
        copyImpl(normalizePath(src, false), normalizePath(dst, true));
    }
    
    protected abstract void copyImpl(String src, String dst) throws IOException;
    
}
