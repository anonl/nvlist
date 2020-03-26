package nl.weeaboo.vn.stats;

import nl.weeaboo.vn.core.IModule;

/**
 * Statistics and analytics module.
 */
public interface IStatsModule extends IModule {

    /** The global resource loading log. */
    IResourceLoadLog getResourceLoadLog();

    /** Logs which resources, text lines, choice options have been seen/used by the player. */
    ISeenLogHolder getSeenLog();

    /** The global play timer */
    IPlayTimer getPlayTimer();

    /** Tracks performance metrics over multiple play sessions. */
    IAnalytics getAnalytics();

}
