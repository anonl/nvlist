package nl.weeaboo.vn.buildgui.task;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;
import javax.swing.SwingUtilities;

import nl.weeaboo.vn.buildgui.SwingUtil;
import nl.weeaboo.vn.buildtools.task.IProgressListener;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

/**
 * Default implementation of {@link ITaskController}.
 */
public final class TaskController implements ITaskController {

    private final CopyOnWriteArrayList<IActiveTaskListener> activeTaskListeners = new CopyOnWriteArrayList<>();

    private final IProgressListener taskFinishListener = new TaskFinishListener();
    private @Nullable ITask activeTask;

    @Override
    public void addActiveTaskListener(IActiveTaskListener listener) {
        activeTaskListeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeActiveTaskListener(IActiveTaskListener listener) {
        activeTaskListeners.remove(Objects.requireNonNull(listener));
    }

    @Override
    public Optional<ITask> getActiveTask() {
        return Optional.ofNullable(activeTask);
    }

    @Override
    public void setActiveTask(@Nullable ITask task) {
        SwingUtil.assertIsEdt();

        if (activeTask != null) {
            activeTask.removeProgressListener(taskFinishListener);
        }

        activeTask = task;

        if (task != null) {
            task.addProgressListener(taskFinishListener);
        }

        activeTaskListeners.forEach(ls -> ls.onActiveTaskChanged(getActiveTask()));
    }

    private final class TaskFinishListener implements IProgressListener {

        @Override
        public void onFinished(TaskResultType resultType, String message) {
            // Remove task-related state when the task finishes
            SwingUtilities.invokeLater(() -> setActiveTask(null));
        }

    }

}
