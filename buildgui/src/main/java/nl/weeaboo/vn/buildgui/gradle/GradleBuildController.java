package nl.weeaboo.vn.buildgui.gradle;

import java.util.Objects;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.buildgui.IBuildController;
import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildtools.project.ProjectModel;
import nl.weeaboo.vn.buildtools.task.AbstractTask;
import nl.weeaboo.vn.buildtools.task.ITask;

public final class GradleBuildController implements IBuildController {

    private static final Logger LOG = LoggerFactory.getLogger(GradleBuildController.class);

    private final ITaskController taskController;
    private final GradleMonitor gradleMonitor;

    public GradleBuildController(ITaskController taskController) {
        this.taskController = Objects.requireNonNull(taskController);

        gradleMonitor = new GradleMonitor();
    }

    @Override
    public void addLogListener(IBuildLogListener listener) {
        gradleMonitor.addLogListener(listener);
    }

    @Override
    public void removeLogListener(IBuildLogListener listener) {
        gradleMonitor.removeLogListener(listener);
    }

    @Override
    public ITask startRunTask() {
        return startTask("run");
    }

    @Override
    public ITask startAssembleDistTask() {
        return startTask("assembleDist");
    }

    private GradleTask startTask(String taskName) {
        GradleTask task = new GradleTask(gradleMonitor);
        taskController.setActiveTask(task);
        task.start(taskName);
        return task;
    }

    @Override
    public void onProjectModelChanged(ProjectModel projectModel) {
        try {
            gradleMonitor.open(projectModel.getFolderConfig());
        } catch (CheckedGradleException e) {
            LOG.error("Error connecting to Gradle build");
        }
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
