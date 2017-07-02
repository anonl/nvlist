package nl.weeaboo.vn.buildgui.task;

import java.util.Optional;

import nl.weeaboo.vn.buildtools.task.ITask;

public interface IActiveTaskListener {

    /**
     * This method is called when a new {@link ITask} is started.
     */
    void onActiveTaskChanged(Optional<ITask> currentTask);

}
