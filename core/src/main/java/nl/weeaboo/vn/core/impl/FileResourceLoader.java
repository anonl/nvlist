package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;

public class FileResourceLoader extends ResourceLoader {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final IEnvironment env;

    private String resourceFolder;

    private transient FileSystemView cachedFileSystemView;

    public FileResourceLoader(IEnvironment env, MediaType mediaType, String resourceFolder) {
        super(mediaType, env.getResourceLoadLog());

        this.env = env;
        this.resourceFolder = Checks.checkNotNull(resourceFolder);
    }

    protected final FileSystemView getFileSystem() {
        if (cachedFileSystemView == null) {
            cachedFileSystemView = new FileSystemView(env.getFileSystem(), resourceFolder);
        }
        return cachedFileSystemView;
    }

    @Override
    protected boolean isValidFilename(String filename) {
        if (filename == null) {
            return false;
        }
        return getFileSystem().getFileExists(filename);
    }

    @Override
    protected List<String> getFiles(String folder) throws IOException {
        List<String> out = new ArrayList<String>();
        getFileSystem().getFiles(out, folder, true);
        return out;
    }

    public String getAbsolutePath(String filename) {
        return resourceFolder + filename;
    }

    public String getResourceFolder() {
        return resourceFolder;
    }

    public void setResourceFolder(String folder) {
        if (!resourceFolder.equals(folder)) {
            cachedFileSystemView = null;
            resourceFolder = folder;
        }
    }

}
