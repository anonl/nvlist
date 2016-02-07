package nl.weeaboo.gdx.res;

import org.slf4j.Logger;

import nl.weeaboo.common.Checks;

abstract class AbstractResourceStore implements IResourceStore {

    private final Logger log;

    public AbstractResourceStore(Logger log) {
        this.log = Checks.checkNotNull(log);
    }

    protected void loadError(String filename, Throwable cause) {
        log.info("Load error: {}", filename, cause);
    }

}
