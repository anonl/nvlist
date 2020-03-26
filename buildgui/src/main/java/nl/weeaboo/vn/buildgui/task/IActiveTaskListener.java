package nl.weeaboo.vn.buildgui.task;

import java.util.Optional;

import nl.weeaboo.vn.buildtools.task.ITask;

/**
 * Listener for changes to the active task.
 */
public interface IActiveTaskListener {

    /**
     * This method is called when a new {@link ITask} is started.
     */
    void onActiveTaskChanged(Optional<ITask> currentTask);

}
