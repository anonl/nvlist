package nl.weeaboo.vn.render;

import java.io.Serializable;

import nl.weeaboo.vn.core.IUpdateable;

public interface IOffscreenRenderTaskBuffer extends Serializable, IUpdateable {

    /**
     * Adds a task to the buffer.
     */
    public void add(IOffscreenRenderTask task);

    /**
     * @return {@code true} if no tasks are currently buffered.
     */
    public boolean isEmpty();

}
