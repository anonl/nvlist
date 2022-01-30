package nl.weeaboo.vn.impl.core;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;

/**
 * Resource loader backed by a {@link IFileSystem}.
 */
public class FileResourceLoader extends ResourceLoader {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final IEnvironment env;

    private FilePath resourceFolder;

    private transient @Nullable FileSystemView cachedFileSystemView;

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
        FileSystemView result = cachedFileSystemView;
        if (result == null) {
            result = new FileSystemView(env.getFileSystem(), resourceFolder);
            cachedFileSystemView = result;
        }
        return result;
    }

    @Override
    protected boolean isValidFilename(FilePath filePath) {
        Checks.checkNotNull(filePath);
        FileSystemView fs = getFileSystem();
        return fs.getFileExists(filePath) && !fs.isFolder(filePath);
    }

    @Override
    protected List<FilePath> getFiles(FilePath folder) {
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
    public final void setResourceFolder(FilePath folder) {
        if (!resourceFolder.equals(folder)) {
            resourceFolder = folder;

            onResourceFolderChanged();
        }
    }

    protected void onResourceFolderChanged() {
        cachedFileSystemView = null;
    }

}
