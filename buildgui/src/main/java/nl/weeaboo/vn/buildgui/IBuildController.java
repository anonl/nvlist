package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildtools.task.ITask;

/**
 * Interface for starting build tasks.
 */
public interface IBuildController extends IProjectModelListener {

    void addLogListener(IBuildLogListener listener);

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
