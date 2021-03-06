package nl.weeaboo.vn.gdx.res;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.filesystem.AbstractFileSystem;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;

/**
 * Adapter between {@link IFileSystem} and {@link FileHandleResolver}.
 */
public abstract class GdxFileSystem extends AbstractFileSystem implements FileHandleResolver {

    private final boolean isReadOnly;

    protected GdxFileSystem(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    protected void closeImpl() {
    }

    @Override
    public boolean isFolder(FilePath path) {
        FileHandle handle = resolve(path);
        return handle != null && handle.isDirectory();
    }

    /**
     * Overridable method so subclasses can avoid calling the (potentially very slow)
     * {@link FileHandle#exists()}.
     */
    protected boolean exists(FilePath path) {
        FileHandle handle = resolve(path.toString());
        return handle != null && handle.exists();
    }

    /**
     * Overridable method so subclasses can avoid calling the (potentially very slow)
     * {@link FileHandle#list()}.
     */
    protected Iterable<FilePath> list(FilePath path, FileHandle handle) {
        FileHandle[] childHandles = handle.list();
        if (childHandles == null) {
            return ImmutableList.of();
        }

        List<FilePath> result = Lists.newArrayList();
        for (FileHandle childHandle : childHandles) {
            FilePath childPath;
            if (childHandle.isDirectory()) {
                childPath = path.resolve(childHandle.name() + "/");
            } else {
                childPath = path.resolve(childHandle.name());
            }
            result.add(childPath);
        }
        return result;
    }

    protected final FileHandle resolveExisting(FilePath path) throws FileNotFoundException {
        if (!exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
        FileHandle file = resolve(path);
        if (file == null) {
            throw new FileNotFoundException(path.toString());
        }
        return file;
    }

    protected final FileHandle resolve(FilePath path) {
        return resolve(path.toString());
    }

    @Override
    protected InputStream openInputStreamImpl(FilePath path) throws IOException {
        return resolveExisting(path).read();
    }

    @Override
    protected boolean getFileExistsImpl(FilePath path) {
        return exists(path);
    }

    @Override
    protected long getFileSizeImpl(FilePath path) throws IOException {
        return resolveExisting(path).length();
    }

    @Override
    protected long getFileModifiedTimeImpl(FilePath path) throws IOException {
        return resolveExisting(path).lastModified();
    }

    @Override
    public Iterable<FilePath> getFiles(FileCollectOptions opts) {
        List<FilePath> result = new ArrayList<>();
        FilePath baseFolderPath = opts.getBaseFolder();
        getFilesImpl(result, baseFolderPath, opts, resolve(baseFolderPath));
        return result;
    }

    private void getFilesImpl(Collection<FilePath> out, FilePath folder, FileCollectOptions opts, FileHandle file) {
        for (FilePath childPath : list(folder, file)) {
            if (isFolder(childPath)) {
                if (opts.isValid(childPath)) {
                    if (opts.collectFolders) {
                        out.add(childPath);
                    }
                    if (opts.recursive) {
                        getFilesImpl(out, childPath, opts, resolve(childPath.toString()));
                    }
                }
            } else {
                if (opts.collectFiles && opts.isValid(childPath)) {
                    out.add(childPath);
                }
            }
        }
    }

}
