package nl.weeaboo.vn.impl.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionIO;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionIO;

/**
 * Base implementation of a resource meta data cache. This cache lazily loads meta on a per-folder basis,
 * reducing I/O by only reading the metadata files once.
 *
 * @see ImageDefinitionIO
 * @see SoundDefinitionIO
 */
public abstract class ResourceMetaDataCache<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<FilePath, T> cache = Maps.newHashMap();
    private final Set<FilePath> seenFolders = Sets.newHashSet();

    /**
     * @return Returns the image definition for the given path, or {@code null} if the given image has no
     *         definition.
     */
    @CheckForNull
    public T getMetaData(FilePath path) {
        T metaData = cache.get(path);
        if (metaData != null) {
            return metaData;
        }

        // Check if we need to load the meta data file for this folder
        FilePath folder = MoreObjects.firstNonNull(path.getParent(), FilePath.empty());
        if (seenFolders.add(folder)) {
            try {
                loadFolderMetadata(folder);

                // Re-attempt to find an meta data (may still fail)
                metaData = cache.get(path);
            } catch (FileNotFoundException fnfe) {
                // Ignore
            } catch (IOException e) {
                logger.warn("Error loading meta data", e);
            }
        }
        return metaData;
    }

    /**
     * Loads the metadata for all files in the given folder.
     */
    protected abstract void loadFolderMetadata(FilePath folder) throws IOException;

    protected final void addToCache(FilePath absolutePath, T metaData) {
        cache.put(absolutePath, metaData);
    }

}
