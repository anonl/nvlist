package nl.weeaboo.vn.buildgui.gradle;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import nl.weeaboo.vn.buildgui.IBuildController;
import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.task.AbstractTask;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

public final class GradleBuildController implements IBuildController {

    private static final Logger LOG = LoggerFactory.getLogger(GradleBuildController.class);

    private final ITaskController taskController;
    private final GradleMonitor gradleMonitor;

    private final CopyOnWriteArrayList<IBuildLogListener> logListeners = new CopyOnWriteArrayList<>();

    public GradleBuildController(ITaskController taskController) {
        this.taskController = Objects.requireNonNull(taskController);

        gradleMonitor = new GradleMonitor();
    }

    @Override
    public void addLogListener(IBuildLogListener listener) {
        logListeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public void removeLogListener(IBuildLogListener listener) {
        logListeners.remove(Objects.requireNonNull(listener));
    }

    @Override
    public ITask startInitProjectTask() {
        // TODO: Actually implement this task in the Gradle build.
        return startTask("initProject");
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
        GradleTask task = new GradleTask(gradleMonitor, logListeners);
        taskController.setActiveTask(task);
        task.start(taskName);
        return task;
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        try {
            gradleMonitor.open(projectModel.getFolderConfig());
        } catch (CheckedGradleException e) {
            LOG.error("Error connecting to Gradle build");
        }
    }

    private static final class GradleTask extends AbstractTask {

        private final GradleMonitor gradleMonitor;
        private final CopyOnWriteArrayList<IBuildLogListener> logListeners;

        private CancellationTokenSource cancelTokenSource;

        GradleTask(GradleMonitor gradleMonitor, Collection<IBuildLogListener> logListeners) {
            this.gradleMonitor = Objects.requireNonNull(gradleMonitor);
            this.logListeners = new CopyOnWriteArrayList<>(logListeners);
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
                    .setStandardOutput(System.out)
                    .addProgressListener(new ProgressListener() {
                        @Override
                        public void statusChanged(ProgressEvent event) {
                            String message = event.getDescription();
                            LOG.debug("[gradle] {}", message);

                            logListeners.forEach(ls -> ls.onLogLine(message));
                            fireProgress(message);
                        }
                    })
                    .withCancellationToken(cancelTokenSource.token())
                    .run(new ResultHandler<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            String message = "Task completed";
                            LOG.info("[gradle] {}", message);

                            logListeners.forEach(ls -> ls.onLogLine(message));
                            fireFinished(TaskResultType.SUCCESS, message);
                        }

                        @Override
                        public void onFailure(GradleConnectionException failure) {
                            String longMessage = "Task failed: " + Throwables.getStackTraceAsString(failure);
                            LOG.warn("[gradle] {}", longMessage);

                            logListeners.forEach(ls -> ls.onLogLine(longMessage));

                            /*
                             * Note: Use the cause's message, since the outer exception's message just says
                             * something generic about a build failure using a specific Gradle distribution
                             */
                            String shortMessage = "Task failed: " + failure.getCause().getMessage();
                            fireFinished(TaskResultType.FAILED, shortMessage);
                        }
                    });
        }
    }

}
