package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.Filenames;
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
    private final LruSet<String> checkedFilenames;

    private String[] autoFileExts = new String[0];
    private boolean checkFileExt = true;

    public ResourceLoader(MediaType mediaType, IResourceLoadLog resourceLoadLog) {
        this.mediaType = Checks.checkNotNull(mediaType);
        this.resourceLoadLog = Checks.checkNotNull(resourceLoadLog);
        this.checkedFilenames = new LruSet<String>(128);
    }

    @Override
    public ResourceId resolveResource(String resourcePath) {
        if (resourcePath == null) {
            return null;
        }

        String filePath = ResourceId.getFilePath(resourcePath);
        String subId = ResourceId.getSubId(resourcePath);
        if (isValidFilename(filePath)) {
            // The given extension works
            return new ResourceId(mediaType, filePath, subId);
        }

        for (String ext : autoFileExts) {
            String fn = Filenames.replaceExt(filePath, ext);
            if (isValidFilename(fn)) {
                // This extension works
                return new ResourceId(mediaType, fn, subId);
            }
        }
        return null;
    }

    public void checkRedundantFileExt(String resourcePath) {
        String filePath = ResourceId.getFilePath(resourcePath);
        if (filePath == null || !checkFileExt) {
            return;
        }

        if (!checkedFilenames.add(filePath)) {
            return;
        }

        // If the file has an extension, isn't valid, but would be valid with a different extension...
        if (!Filenames.getExtension(filePath).isEmpty() && !isValidFilename(filePath)) {
            ResourceId resourceId = resolveResource(filePath);
            if (resourceId != null && isValidFilename(resourceId.getFilePath())) {
                LOG.warn("Incorrect file extension: {}", filePath);
            }
        }

        //Check if a file extension in the default list has been specified.
        for (String ext : autoFileExts) {
            if (filePath.endsWith("." + ext)) {
                if (isValidFilename(filePath)) {
                    LOG.debug("You don't need to specify the file extension: {}", filePath);
                }
                break;
            }
        }
    }

    public void preload(String filename) {
        preload(filename, false);
    }

    public void preload(String filename, boolean suppressErrors) {
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

    /**
     * @param filename A normalized filename
     */
    protected abstract boolean isValidFilename(String filename);

    public Collection<String> getMediaFiles(String folder) {
        try {
            Collection<String> files = getFiles(folder);
            List<String> filtered = new ArrayList<String>(files.size());
            for (String file : files) {
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

    protected abstract List<String> getFiles(String folder) throws IOException;

    public void setAutoFileExts(String... exts) {
        autoFileExts = exts.clone();
    }

}
