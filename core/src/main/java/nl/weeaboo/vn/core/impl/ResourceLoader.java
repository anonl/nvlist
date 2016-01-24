package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public abstract class ResourceLoader implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    private final IResourceLoadLog resourceLoadLog;
    private final LruSet<String> checkedFilenames;

    private String[] autoFileExts = new String[0];
    private boolean checkFileExt;

    public ResourceLoader(IResourceLoadLog resourceLoadLog) {
        this.resourceLoadLog = Checks.checkNotNull(resourceLoadLog);
        this.checkedFilenames = new LruSet<String>(128);
    }

    //Functions
    protected String replaceExt(String filename, String ext) {
        return CoreImpl.replaceExt(filename, ext);
    }

    public String normalizeFilename(String filename) {
        if (filename == null) return null;

        if (isValidFilename(filename)) {
            return filename; //The given extension works
        }

        for (String ext : autoFileExts) {
            String fn = replaceExt(filename, ext);
            if (isValidFilename(fn)) {
                return fn; //This extension works
            }
        }

        return null;
    }

    public void checkRedundantFileExt(String filename) {
        if (filename == null || !checkFileExt) {
            return;
        }

        if (!checkedFilenames.add(filename)) {
            return;
        }

        //Check if a file extension in the default list has been specified.
        for (String ext : autoFileExts) {
            if (filename.endsWith("." + ext)) {
                if (isValidFilename(filename)) {
                    LOG.debug("You don't need to specify the file extension: {}", filename);
                } else if (isValidFilename(normalizeFilename(filename))) {
                    LOG.warn("Incorrect file extension: {}", filename);
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

        String normalized = normalizeFilename(filename);
        if (normalized != null) {
            preloadNormalized(normalized);
        }
    }

    /**
     * @param normalizedFilename The normalized filename of the resource to preload.
     */
    protected void preloadNormalized(String normalizedFilename) {
        // Default implementation does nothing
    }

    public void logLoad(ResourceLoadInfo info) {
        resourceLoadLog.logLoad(info);
    }

    //Getters
    /**
     * @param normalizedFilename A normalized filename
     */
    protected abstract boolean isValidFilename(String normalizedFilename);

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

    //Setters
    public void setAutoFileExts(String... exts) {
        autoFileExts = exts.clone();
    }

}
