package nl.weeaboo.gdx.res;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.AbstractFileSystem;
import nl.weeaboo.filesystem.FileCollectOptions;

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
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        return file;
    }

    @Override
    protected InputStream openInputStreamImpl(String path) throws IOException {
        return resolveExisting(path).read();
    }

    @Override
    protected boolean getFileExistsImpl(String path) {
        return resolve(path).exists();
    }

    @Override
    protected long getFileSizeImpl(String path) throws IOException {
        return resolveExisting(path).length();
    }

    @Override
    protected long getFileModifiedTimeImpl(String path) throws IOException {
        return resolveExisting(path).lastModified();
    }

    @Override
    protected void getFiles(Collection<String> out, String prefix, FileCollectOptions opts) throws IOException {
        getFilesImpl(out, prefix, opts, resolveExisting(prefix));
    }

    private void getFilesImpl(Collection<String> out, String prefix, FileCollectOptions opts,
            FileHandle file) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            // Append folder name to prefix
            if (prefix.length() > 0 && !prefix.endsWith("/")) {
                prefix += "/" + file.name();
            } else {
                prefix += file.name();
            }

            for (FileHandle child : file.list()) {
                boolean isDirectory = child.isDirectory();
                if ((isDirectory && opts.collectFolders) || (!isDirectory && opts.collectFiles)) {
                    out.add(prefix + "/" + child.name());

                    if (isDirectory && opts.recursive) {
                        getFilesImpl(out, prefix, opts, file);
                    }
                }
            }
        } else if (opts.collectFiles) {
            out.add(prefix);
        }
    }

}
