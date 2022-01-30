package nl.weeaboo.vn.impl.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.stats.IResourceLoadLog;

/**
 * Base implementation for resource loaders.
 */
@CustomSerializable
public abstract class ResourceLoader implements IResourceResolver {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    private final MediaType mediaType;
    private final IResourceLoadLog resourceLoadLog;

    private transient LruSet<FilePath> checkedRedundantFilenames;
    private transient LruSet<FilePath> unresolvableFilenames;
    private transient LoadingCache<FilePath, ResourceId> resolveCache;

    private @Nullable IPreloadHandler preloadHandler;

    private String[] autoFileExts = new String[0];
    private boolean checkFileExt = true;

    protected ResourceLoader(MediaType mediaType, IResourceLoadLog resourceLoadLog) {
        this.mediaType = Checks.checkNotNull(mediaType);
        this.resourceLoadLog = Checks.checkNotNull(resourceLoadLog);

        initTransients();
    }

    private void initTransients() {
        checkedRedundantFilenames = new LruSet<>(128);
        unresolvableFilenames = new LruSet<>(128);
        resolveCache = buildResolveCache();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    private void resetUnresolvableFilenames() {
        unresolvableFilenames.clear();
    }

    private LoadingCache<FilePath, ResourceId> buildResolveCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new FileResolveFunction());
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath path) {
        Checks.checkNotNull(path);
        if (unresolvableFilenames.contains(path)) {
            return null;
        }

        try {
            return resolveCache.get(path);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                LOG.trace("Resource ({}) not found '{}'", mediaType, path, e.getCause());
            } else {
                LOG.warn("Error loading resource ({}) '{}'", mediaType, path, e.getCause());
            }
            return null;
        }
    }

    /**
     * Logs a warning if the given resource path redundantly includes a file extension.
     */
    public EFileExtCheckResult checkRedundantFileExt(FilePath resourcePath) {
        if (!checkFileExt) {
            return EFileExtCheckResult.DISABLED;
        }

        FilePath filePath = ResourceId.extractFilePath(resourcePath.toString());
        if (!checkedRedundantFilenames.add(filePath)) {
            return EFileExtCheckResult.ALREADY_CHECKED;
        }

        // If the file has an extension, isn't valid, but would be valid with a different extension...
        ResourceId resourceId = resolveResource(filePath);
        if (resourceId == null) {
            // Maybe we couldn't find the resource is because the file extension is incorrectly capitalized?
            for (String ext : autoFileExts) {
                if (filePath.getName().toLowerCase(Locale.ROOT).endsWith("." + ext.toLowerCase(Locale.ROOT))) {
                    LOG.warn("File extension has incorrect capitalization: {}, expected: {}",
                            filePath, Arrays.asList(autoFileExts));
                }
            }
            return EFileExtCheckResult.INVALID;
        }

        if (!filePath.getExt().isEmpty() && !isValidFilename(filePath) && isValidFilename(resourceId.getFilePath())) {
            LOG.warn("Incorrect file extension: {}", filePath);
            return EFileExtCheckResult.INVALID;
        }

        // Check if a file extension in the default list has been specified.
        for (String ext : autoFileExts) {
            if (filePath.getName().endsWith("." + ext)) {
                LOG.debug("You don't need to specify the file extension: {}", filePath);
                return EFileExtCheckResult.REDUNDANT;
            }
        }

        return EFileExtCheckResult.OK;
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
        if (preloadHandler == null) {
            // Default implementation does nothing
            LOG.trace("Preload (no-op implementation): {}", resourceId);
        } else {
            LOG.trace("Preload: {}", resourceId);
            preloadHandler.preloadNormalized(resourceId);
        }
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
            return Collections.unmodifiableList(filtered);
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
        resetUnresolvableFilenames();
    }

    /** Sets the function that handles calls to {@link #preloadNormalized(nl.weeaboo.vn.core.ResourceId)}. */
    public void setPreloadHandler(IPreloadHandler preloadHandler) {
        this.preloadHandler = Checks.checkNotNull(preloadHandler);
    }

    /**
     * Toggles an automatic check for redundant/incorrect file extensions in filenames passed to this resource loader.
     */
    protected void setCheckFileExts(boolean check) {
        checkFileExt = check;
    }

    private final class FileResolveFunction extends CacheLoader<FilePath, ResourceId> {

        @Override
        public ResourceId load(FilePath path) throws FileNotFoundException {
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

            throw new FileNotFoundException(path.toString());
        }

    }

    /**
     * Result of a file extension check.
     */
    public enum EFileExtCheckResult {
        DISABLED,
        ALREADY_CHECKED,
        REDUNDANT,
        INVALID,
        OK
    }

}
