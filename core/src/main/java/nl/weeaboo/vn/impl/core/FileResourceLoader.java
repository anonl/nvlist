package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;

public class FileResourceLoader extends ResourceLoader {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final IEnvironment env;

    private FilePath resourceFolder;

    private transient FileSystemView cachedFileSystemView;

    public FileResourceLoader(IEnvironment env, MediaType mediaType) {
        this(env, mediaType, mediaType.getSubFolder());
    }

    public FileResourceLoader(IEnvironment env, MediaType mediaType, FilePath resourceFolder) {
        super(mediaType, env.getResourceLoadLog());

        this.env = Checks.checkNotNull(env);
        this.resourceFolder = Checks.checkNotNull(resourceFolder);
    }

    /**
     * @return The current file system view used by this resource loader. Over time, the returned filesystem
     *         may change or point to a different (sub)folder.
     */
    public final FileSystemView getFileSystem() {
        if (cachedFileSystemView == null) {
            cachedFileSystemView = new FileSystemView(env.getFileSystem(), resourceFolder);
        }
        return cachedFileSystemView;
    }

    @Override
    protected boolean isValidFilename(FilePath filePath) {
        if (filePath == null) {
            return false;
        }
        return getFileSystem().getFileExists(filePath);
    }

    @Override
    protected List<FilePath> getFiles(FilePath folder) throws IOException {
        FileCollectOptions opts = FileCollectOptions.files(folder);
        return ImmutableList.copyOf(getFileSystem().getFiles(opts));
    }

    /** Resolves a relative path to an absolute path. */
    public FilePath getAbsolutePath(FilePath relPath) {
        return resourceFolder.resolve(relPath);
    }

    /** Returns the base folder. Resource paths are resolved relative to this folder. */
    public FilePath getResourceFolder() {
        return resourceFolder;
    }

    /** Sets the base folder. Resource paths are resolved relative to this folder. */
    public void setResourceFolder(FilePath folder) {
        if (!resourceFolder.equals(folder)) {
            cachedFileSystemView = null;
            resourceFolder = folder;
        }
    }

}
