package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IResourceResolver extends Serializable {

    /**
     * @return The canonical filename of the resource, or {@code null} if the resource could not be found.
     */
    ResourceId resolveResource(String filename);

}
