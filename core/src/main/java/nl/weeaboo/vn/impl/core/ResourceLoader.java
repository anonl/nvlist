package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public abstract class ResourceLoader implements IResourceResolver {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    private final MediaType mediaType;
    private final IResourceLoadLog resourceLoadLog;
    private final LruSet<FilePath> checkedFilenames;

    private String[] autoFileExts = new String[0];
    private boolean checkFileExt = true;

    public ResourceLoader(MediaType mediaType, IResourceLoadLog resourceLoadLog) {
        this.mediaType = Checks.checkNotNull(mediaType);
        this.resourceLoadLog = Checks.checkNotNull(resourceLoadLog);
        this.checkedFilenames = new LruSet<>(128);
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath path) {
        if (path == null) {
            return null;
        }

        FilePath filePath = ResourceId.extractFilePath(path.toString());
        String subId = ResourceId.extractSubId(path.getName());
        if (isValidFilename(filePath)) {
            // The given extension works
            return new ResourceId(mediaType, filePath, subId);
        }

        for (String ext : autoFileExts) {
            FilePath fn = filePath.withExt(ext);
            if (isValidFilename(fn)) {
                // This extension works
                return new ResourceId(mediaType, fn, subId);
            }
        }
        return null;
    }

    /**
     * Logs a warning if the given resource path redundantly includes a file extension.
     */
    public void checkRedundantFileExt(FilePath resourcePath) {
        FilePath filePath = ResourceId.extractFilePath(resourcePath.toString());
        if (filePath == null || !checkFileExt) {
            return;
        }

        if (!checkedFilenames.add(filePath)) {
            return;
        }

        // If the file has an extension, isn't valid, but would be valid with a different extension...
        if (!filePath.getExt().isEmpty() && !isValidFilename(filePath)) {
            ResourceId resourceId = resolveResource(filePath);
            if (resourceId != null && isValidFilename(resourceId.getFilePath())) {
                LOG.warn("Incorrect file extension: {}", filePath);
            }
        }

        //Check if a file extension in the default list has been specified.
        for (String ext : autoFileExts) {
            if (filePath.getName().endsWith("." + ext)) {
                if (isValidFilename(filePath)) {
                    LOG.debug("You don't need to specify the file extension: {}", filePath);
                }
                break;
            }
        }
    }

    /**
     * Attempts to preload the specified file, logging an error if the filename is invalid.
     */
    public void preload(FilePath filename) {
        preload(filename, false);
    }

    /**
     * Attempts to preload the specified file.
     * @param suppressErrors If {@code true}, logs an error if the filename is invalid.
     */
    public void preload(FilePath filename, boolean suppressErrors) {
        if (!suppressErrors) {
            checkRedundantFileExt(filename);
        }

        ResourceId resourceId = resolveResource(filename);
        if (resourceId != null) {
            preloadNormalized(resourceId);
        }
    }

    /**
     * @param resourceId Canonical identifier of the resource to preload.
     */
    protected void preloadNormalized(ResourceId resourceId) {
        // Default implementation does nothing
    }

    /** Logs a resource load event. */
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
        resourceLoadLog.logLoad(resourceId, info);
    }

    protected abstract boolean isValidFilename(FilePath filePath);

    /**
     * Returns all resource files in the folder. Only returns resources of the file types that this resource loader is
     * interested in.
     */
    public Collection<FilePath> getMediaFiles(FilePath folder) {
        try {
            Collection<FilePath> files = getFiles(folder);
            List<FilePath> filtered = new ArrayList<>(files.size());
            for (FilePath file : files) {
                if (isValidFilename(file)) {
                    filtered.add(file);
                }
            }
            return filtered;
        } catch (IOException ioe) {
            LOG.warn("Folder doesn't exist or can't be read: {}", folder, ioe);
            return Collections.emptyList();
        }
    }

    protected abstract List<FilePath> getFiles(FilePath folder) throws IOException;

    /**
     * Sets some file extensions to append to the user-supplied path when attempting to load a resource file. This
     * allows use to write "myfolder/myimage" in the script, without having to care whether the image is stored as
     * {@code .jng} or {@code .png}.
     */
    public void setAutoFileExts(String... exts) {
        autoFileExts = exts.clone();
    }

}
