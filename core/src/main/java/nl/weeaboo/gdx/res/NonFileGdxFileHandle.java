package nl.weeaboo.gdx.res;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Base class for {@link FileHandle} subclasses that aren't based on a {@link java.io.File}.
 */
public abstract class NonFileGdxFileHandle extends FileHandle {

    public NonFileGdxFileHandle(String path, FileType type) {
        super(path, type);
    }

    @Override
    public abstract InputStream read();

    @Override
    public abstract FileHandle parent();

    @Override
    public abstract FileHandle child(String name);

    @Override
    public FileHandle sibling(String name) {
        return parent().child(name);
    }

    @Override
    public abstract boolean isDirectory();

    @Override
    public abstract FileHandle[] list();

    @Override
    public FileHandle[] list(FileFilter filter) {
        return list();
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        return list();
    }

    @Override
    public FileHandle[] list(String suffix) {
        return list();
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    @Override
    public abstract long length();

    @Override
    public abstract boolean exists();

    @Override
    public OutputStream write(boolean append) {
        throw gdxException("Unable to modify " + this);
    }

    @Override
    public Writer writer(boolean append, String charset) {
        throw gdxException("Unable to modify " + this);
    }

    @Override
    public boolean delete() {
        throw gdxException("Unable to modify " + this);
    }

    @Override
    public boolean deleteDirectory() {
        throw gdxException("Unable to modify " + this);
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        throw gdxException("Unable to modify " + this);
    }

    protected static GdxRuntimeException gdxException(String message) {
        return new GdxRuntimeException(message);
    }

    protected static GdxRuntimeException gdxException(Exception exception) {
        return new GdxRuntimeException(exception);
    }

}
