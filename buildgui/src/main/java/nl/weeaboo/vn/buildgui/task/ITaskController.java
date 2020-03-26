package nl.weeaboo.vn.buildgui.task;

import java.util.Optional;

import javax.annotation.Nullable;

import nl.weeaboo.vn.buildtools.task.ITask;

/**
 * Changes/inspects running tasks.
 */
public interface ITaskController {

    /**
     * Adds a listener for changes to the running task.
     */
    void addActiveTaskListener(IActiveTaskListener listener);

    /**
     * Removes a listener previously added using {@link #addActiveTaskListener(IActiveTaskListener)}.
     */
    void removeActiveTaskListener(IActiveTaskListener listener);

    /**
     * Returns the active task ({@link Optional#empty()} if no task is currently active).
     */
    Optional<ITask> getActiveTask();

    /**
     * Sets the active task.
     */
    void setActiveTask(@Nullable ITask task);

}
