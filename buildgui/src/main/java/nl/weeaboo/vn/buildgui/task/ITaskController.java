package nl.weeaboo.vn.buildgui.task;

import java.util.Optional;

import javax.annotation.Nullable;

import nl.weeaboo.vn.buildtools.task.ITask;

public interface ITaskController {

    void addActiveTaskListener(IActiveTaskListener listener);

    void removeActiveTaskListener(IActiveTaskListener listener);

    Optional<ITask> getActiveTask();

    void setActiveTask(@Nullable ITask task);

}
