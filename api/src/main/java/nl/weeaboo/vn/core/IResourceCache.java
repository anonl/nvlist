package nl.weeaboo.vn.core;

/**
 * Cached resources.
 */
public interface IResourceCache {

    /**
     * Clears (resource) caches. This is automatically done during a restart.
     */
    void clearCaches();

}
