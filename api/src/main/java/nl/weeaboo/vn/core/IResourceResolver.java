package nl.weeaboo.vn.core;

import java.io.Serializable;

import javax.annotation.Nullable;

import nl.weeaboo.filesystem.FilePath;

/**
 * Finds the accompanying resource for a file path.
 */
public interface IResourceResolver extends Serializable {

    /**
     * @return The canonical filename of the resource, or {@code null} if the resource could not be found.
     */
    @Nullable ResourceId resolveResource(FilePath resourcePath);

}
