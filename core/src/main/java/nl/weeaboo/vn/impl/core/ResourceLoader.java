package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public ResourceId resolveResource(FilePath path) {
        if (path == null) {
            return null;
        }

        FilePath filePath = ResourceId.getFilePath(path.toString());
        String subId = ResourceId.getSubId(path.getName());
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

    public void checkRedundantFileExt(FilePath resourcePath) {
        FilePath filePath = ResourceId.getFilePath(resourcePath.toString());
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

    public void preload(FilePath filename) {
        preload(filename, false);
    }

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

    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
        resourceLoadLog.logLoad(resourceId, info);
    }

    protected abstract boolean isValidFilename(FilePath filePath);

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

    public void setAutoFileExts(String... exts) {
        autoFileExts = exts.clone();
    }

}
