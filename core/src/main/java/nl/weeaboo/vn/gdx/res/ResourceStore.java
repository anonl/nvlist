package nl.weeaboo.vn.gdx.res;

import org.slf4j.Logger;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IDestructible;

/**
 * Loads and caches resources.
 */
public abstract class ResourceStore implements IDestructible {

    private final Logger log;

    private boolean destroyed;

    public ResourceStore(Logger log) {
        this.log = Checks.checkNotNull(log);
    }

    @Override
    public void destroy() {
        destroyed = true;
        clear();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    /** Disposes all resources associated with this resource store. */
    public abstract void clear();

    protected void loadError(FilePath path, Throwable cause) {
        log.info("Load error: {}", path, cause);
    }

}
