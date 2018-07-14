package nl.weeaboo.vn.gdx.res;

import org.slf4j.Logger;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

public abstract class AbstractResourceStore implements IResourceStore {

    private final Logger log;

    private boolean destroyed;

    public AbstractResourceStore(Logger log) {
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

    protected void loadError(FilePath path, Throwable cause) {
        log.info("Load error: {}", path, cause);
    }

}
