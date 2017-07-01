package nl.weeaboo.vn.buildgui.gradle;

import java.util.Objects;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ResultHandler;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.buildgui.IBuildController;
import nl.weeaboo.vn.buildgui.task.AbstractTask;
import nl.weeaboo.vn.buildgui.task.ITask;
import nl.weeaboo.vn.buildgui.task.ITaskController;

public final class GradleBuildController implements IBuildController {

    private final GradleMonitor gradleMonitor;
    private final ITaskController taskController;

    public GradleBuildController(GradleMonitor gradleMonitor, ITaskController taskController) {
        this.taskController = taskController;
        this.gradleMonitor = Objects.requireNonNull(gradleMonitor);
    }

    @Override
    public ITask startRunTask() {
        return startTask("run");
    }

    private GradleTask startTask(String taskName) {
        GradleTask task = new GradleTask(gradleMonitor);
        taskController.setActiveTask(task);
        task.start(taskName);
        return task;
    }

    private static final class GradleTask extends AbstractTask {

        private final GradleMonitor gradleMonitor;

        private CancellationTokenSource cancelTokenSource;

        GradleTask(GradleMonitor gradleMonitor) {
            this.gradleMonitor = Objects.requireNonNull(gradleMonitor);
        }

        @Override
        public void cancel() {
            cancelTokenSource.cancel();

            super.cancel();
        }

        void start(String taskName) {
            Preconditions.checkState(cancelTokenSource == null, "Task is already running");

            cancelTokenSource = GradleConnector.newCancellationTokenSource();

            gradleMonitor.buildLauncher(taskName)
                    .addProgressListener(new ProgressListener() {
                        @Override
                        public void statusChanged(ProgressEvent event) {
                            fireProgress(event.getDescription());
                        }
                    })
                    .withCancellationToken(cancelTokenSource.token())
                    .run(new ResultHandler<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            fireFinished();
                        }

                        @Override
                        public void onFailure(GradleConnectionException failure) {
                            fireProgress("FAILED: " + failure.getMessage());
                            fireFinished();
                        }
                    });
        }
    }
}
