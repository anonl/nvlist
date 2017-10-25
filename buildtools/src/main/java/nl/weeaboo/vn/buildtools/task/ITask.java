package nl.weeaboo.vn.buildtools.task;

/**
 * Represents a long-running tasks.
 */
public interface ITask {

    /**
     * Request cancellation of the task.
     */
    void cancel();

    /**
     * Adds a task progress listener.
     */
    void addProgressListener(IProgressListener listener);

    /**
     * Removes a listener previously added using {@link #addProgressListener(IProgressListener)}.
     */
    void removeProgressListener(IProgressListener listener);

}
