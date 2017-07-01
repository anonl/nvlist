package nl.weeaboo.vn.buildgui.task;

import java.util.Optional;

public interface IActiveTaskListener {

    /**
     * This method is called when a new {@link ITask} is started.
     */
    void onActiveTaskChanged(Optional<ITask> currentTask);

}
