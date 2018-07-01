package nl.weeaboo.vn.gdx.res;

import nl.weeaboo.vn.core.IDestructible;

public interface IResourceStore extends IDestructible {

    /** Disposes all resources associated with this resource store. */
    void clear();

}
