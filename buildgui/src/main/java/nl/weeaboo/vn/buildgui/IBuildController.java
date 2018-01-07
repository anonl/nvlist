package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildtools.task.ITask;

/**
 * Interface for starting build tasks.
 */
public interface IBuildController extends IProjectModelListener {

    /**
     * Adds a listener to track build progress.
     */
    void addLogListener(IBuildLogListener listener);

    /**
     * Removes a listener previously added through {@link #addLogListener(IBuildLogListener)}.
     */
    void removeLogListener(IBuildLogListener listener);

    /**
     * Creates an empty NVList project in the current project folder.
     */
    ITask startInitProjectTask();

    /**
     * Runs the current NVList project.
     */
    ITask startRunTask();

    /**
     * Builds a distribution of the current NVList project.
     */
    ITask startAssembleDistTask();

}
