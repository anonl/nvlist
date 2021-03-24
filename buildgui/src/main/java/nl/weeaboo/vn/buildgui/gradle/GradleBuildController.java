package nl.weeaboo.vn.buildgui.gradle;

import java.awt.Color;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ResultHandler;
import org.gradle.tooling.events.ProgressEvent;
import org.gradle.tooling.events.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import nl.weeaboo.vn.buildgui.IBuildController;
import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.task.Task;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

/**
 * Default implementation of {@link IBuildController}.
 */
public final class GradleBuildController implements IBuildController {

    private static final Logger LOG = LoggerFactory.getLogger(GradleBuildController.class);

    private static final String UNKNOWN_VERSION = "unknown";

    private final ITaskController taskController;
    private final GradleMonitor gradleMonitor;

    private final CopyOnWriteArrayList<IBuildLogListener> logListeners = new CopyOnWriteArrayList<>();

    private ProjectFolderConfig folderConfig = new ProjectFolderConfig();
    private String nvlistVersion = UNKNOWN_VERSION;

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
    public ITask startInitProject() {
        /*
         * TODO: Actually implement this task in the Gradle build (then make the create project button visible
         * again).
         */
        return startTask("initProject");
    }

    @Override
    public ITask startRun() {
        return startTask("run");
    }

    @Override
    public ITask startCreateRelease() {
        return startTask("cleanArtifacts", "archiveArtifacts");
    }

    @Override
    public ITask startCheckForUpdates() {
        CheckForUpdatesTask task = new CheckForUpdatesTask(nvlistVersion, logListeners);
        taskController.setActiveTask(task);
        task.start();
        return task;
    }

    @Override
    public ITask startResourceOptimizer() {
        return startTask("optimizeResources");
    }

    private GradleTask startTask(String... taskNames) {
        GradleTask task = new GradleTask(gradleMonitor, logListeners);
        taskController.setActiveTask(task);
        task.start(taskNames);
        return task;
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        folderConfig = projectModel.getFolderConfig();
        nvlistVersion = projectModel.getBuildProperty("nvlistVersion", UNKNOWN_VERSION);

        try {
            gradleMonitor.open(projectModel.getFolderConfig());
        } catch (CheckedGradleException e) {
            LOG.error("Error connecting to Gradle build");
        }
    }

    @Override
    public ProjectFolderConfig getFolderConfig() {
        return folderConfig;
    }

    private static final class GradleTask extends Task {

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

        void start(String... taskNames) {
            Preconditions.checkState(cancelTokenSource == null, "Task is already running");

            cancelTokenSource = GradleConnector.newCancellationTokenSource();

            gradleMonitor.buildLauncher(taskNames)
                    .setStandardOutput(new OutputToLogAdapter(this::fireLogLine))
                    .setStandardError(new OutputToLogAdapter(this::fireLogLine))
                    .addProgressListener(new ProgressListener() {
                        @Override
                        public void statusChanged(ProgressEvent event) {
                            String message = event.getDisplayName();

                            LOG.debug("[gradle] {}", message);

                            fireLogLine(message, LogStyles.DEBUG_COLOR);
                            fireProgress(message);
                        }
                    })
                    .withCancellationToken(cancelTokenSource.token())
                    .run(new ResultHandler<Void>() {
                        @Override
                        public void onComplete(Void result) {
                            String message = "Task completed";
                            LOG.info("[gradle] {}", message);

                            fireLogLine(message, LogStyles.GRADLE_COMPLETE_COLOR);
                            fireFinished(TaskResultType.SUCCESS, message);
                        }

                        @Override
                        public void onFailure(GradleConnectionException failure) {
                            String longMessage = "Task failed: " + Throwables.getStackTraceAsString(failure);
                            LOG.warn("[gradle] {}", longMessage);

                            fireLogLine(longMessage, LogStyles.GRADLE_FAILED_COLOR);

                            /*
                             * Note: Use the cause's message, since the outer exception's message just says
                             * something generic about a build failure using a specific Gradle distribution
                             */
                            String shortMessage = "Task failed: ";
                            if (failure.getCause() == null) {
                                shortMessage += failure.getMessage();
                            } else {
                                shortMessage += failure.getCause().getMessage();
                            }
                            fireFinished(TaskResultType.FAILED, shortMessage);
                        }
                    });
        }

        private void fireLogLine(String line, Color color) {
            logListeners.forEach(ls -> ls.onLogLine(line, color));
        }
    }

}
