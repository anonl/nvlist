package nl.weeaboo.vn.gdx.res;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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

    protected abstract Iterable<FileHandle> listChildren();

    private FileHandle[] filteredChildren(Predicate<FileHandle> predicate) {
        return Iterables.toArray(Iterables.filter(listChildren(), predicate), FileHandle.class);
    }

    @Override
    public final FileHandle[] list() {
        return Iterables.toArray(listChildren(), FileHandle.class);
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        return filteredChildren(new Predicate<FileHandle>() {
            @Override
            public boolean apply(FileHandle handle) {
                return filter.accept(handle.file());
            }
        });
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        return filteredChildren(new Predicate<FileHandle>() {
            @Override
            public boolean apply(FileHandle handle) {
                return filter.accept(handle.file().getParentFile(), handle.name());
            }
        });
    }

    @Override
    public FileHandle[] list(String suffix) {
        return filteredChildren(new Predicate<FileHandle>() {
            @Override
            public boolean apply(FileHandle handle) {
                return handle.name().endsWith(suffix);
            }
        });
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
