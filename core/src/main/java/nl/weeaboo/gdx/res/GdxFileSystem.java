package nl.weeaboo.gdx.res;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.AbstractFileSystem;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;

public class GdxFileSystem extends AbstractFileSystem implements FileHandleResolver {

    private final String prefix;
    private final boolean isReadOnly;

    public GdxFileSystem(String prefix, boolean isReadOnly) {
        this.prefix = Checks.checkNotNull(prefix);
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
    public FileHandle resolve(String path) {
        return Gdx.files.internal(prefix + path);
    }

    protected FileHandle resolveExisting(String path) throws FileNotFoundException {
        FileHandle file = resolve(path);
        // TODO #32: exists() is very slow
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        return file;
    }

    @Override
    protected InputStream openInputStreamImpl(FilePath path) throws IOException {
        return resolveExisting(path.toString()).read();
    }

    @Override
    protected boolean getFileExistsImpl(FilePath path) {
        // TODO #32: exists() is very slow
        return resolve(path.toString()).exists();
    }

    @Override
    protected long getFileSizeImpl(FilePath path) throws IOException {
        return resolveExisting(path.toString()).length();
    }

    @Override
    protected long getFileModifiedTimeImpl(FilePath path) throws IOException {
        return resolveExisting(path.toString()).lastModified();
    }

    @Override
    public Iterable<FilePath> getFiles(FileCollectOptions opts) throws IOException {
        List<FilePath> result = new ArrayList<>();
        getFilesImpl(result, opts.prefix, opts,
                resolveExisting(opts.prefix.toString()));
        return result;
    }

    private void getFilesImpl(Collection<FilePath> out, FilePath path, FileCollectOptions opts,
            FileHandle file) {

        // TODO #32: exists() is very slow
        if (!file.exists()) {
            return;
        }

        // TODO #32: isDirectory() calls list() and is therefore very slow
        if (file.isDirectory()) {

            // TODO #32: list() is very slow
            for (FileHandle child : file.list()) {
                FilePath childPath;
                if (child.isDirectory()) {
                    childPath = path.resolve(child.name() + "/");
                } else {
                    childPath = path.resolve(child.name());
                }

                if (childPath.isFolder() && opts.collectFolders) {
                    out.add(childPath);

                    if (opts.recursive) {
                        getFilesImpl(out, childPath, opts, child);
                    }
                } else if (!childPath.isFolder() && opts.collectFiles) {
                    out.add(childPath);
                }
            }
        } else if (opts.collectFiles) {
            out.add(path);
        }
    }

}
