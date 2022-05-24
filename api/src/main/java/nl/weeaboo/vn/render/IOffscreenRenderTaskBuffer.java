package nl.weeaboo.vn.render;

import java.io.Serializable;

import nl.weeaboo.vn.core.IUpdateable;

/**
 * Queues {@link IOffscreenRenderTask} to be completed later.
 */
public interface IOffscreenRenderTaskBuffer extends Serializable, IUpdateable {

    /**
     * Adds a task to the buffer.
     */
    void add(IOffscreenRenderTask task);

    /**
     * @return {@code true} if no tasks are currently buffered.
     */
    boolean isEmpty();

}
