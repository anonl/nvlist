package nl.weeaboo.vn.video.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.ResourceLoader;

public class VideoResourceLoader extends ResourceLoader {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    private final IEnvironment env;

    private String videoFolder = "video/";

    private transient FileSystemView cachedFileSystemView;

    public VideoResourceLoader(IEnvironment env) {
        this.env = env;
    }

    protected final FileSystemView getFileSystem() {
        if (cachedFileSystemView == null) {
            cachedFileSystemView = new FileSystemView(env.getFileSystem(), videoFolder);
        }
        return cachedFileSystemView;
    }

    @Override
    protected void preloadNormalized(String filename) {
        // Do nothing
    }

    @Override
    protected boolean isValidFilename(String filename) {
        return filename != null && getFileSystem().getFileExists(filename);
    }

    @Override
    protected List<String> getFiles(String folder) throws IOException {
        List<String> out = new ArrayList<String>();
        getFileSystem().getFiles(out, folder, true);
        return out;
    }

    public void setVideoFolder(String folder) {
        if (!videoFolder.equals(folder)) {
            cachedFileSystemView = null;
        }
    }

}
