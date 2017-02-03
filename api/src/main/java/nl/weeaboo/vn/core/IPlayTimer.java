package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.vn.save.IStorage;

public interface IPlayTimer extends IUpdateable, Serializable {

    /**
     * Loads previously saved state from the supplied storage.
     * @throws IOException If an I/O error occurs while reading from storage.
     */
    void load(IStorage storage) throws IOException;

    /**
     * Saves the current state to the supplied storage.
     * @throws IOException If an I/O error occurs while writing to storage.
     */
    void save(IStorage storage) throws IOException;

    /**
     * Total play time, excluding idle time.
     *
     * @see #getIdleTime()
     */
    Duration getTotalPlayTime();

    /**
     * If the player is idle for a while (no input), the idle timer increases rather than the total play timer.
     *
     * @see #getTotalPlayTime()
     */
    Duration getIdleTime();

}
