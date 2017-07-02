package nl.weeaboo.vn.buildgui.gradle;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ProjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

public final class GradleMonitor implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GradleMonitor.class);

    private final CopyOnWriteArrayList<IBuildLogListener> logListeners = new CopyOnWriteArrayList<>();

    private ProjectConnection connection;
    private ProjectFolderConfig folderConfig;

    public GradleMonitor() {
    }

    public void open(ProjectFolderConfig folderConfig) throws CheckedGradleException {
        if (connection != null) {
            connection.close();
        }

        try {
            connection = GradleConnector.newConnector()
                    .useBuildDistribution()
                    .forProjectDirectory(folderConfig.getBuildToolsFolder())
                    .connect();
        } catch (GradleConnectionException e) {
            throw new CheckedGradleException("Error connecting to Gradle: folderConfig=" + folderConfig, e);
        }

        this.folderConfig = folderConfig;
    }

    @Override
    public void close() {
        connection.close();
    }

    public void addLogListener(IBuildLogListener listener) {
        logListeners.add(Objects.requireNonNull(listener));
    }

    public void removeLogListener(IBuildLogListener listener) {
        logListeners.remove(Objects.requireNonNull(listener));
    }

    <T> ModelBuilder<T> modelBuilder(Class<T> type) {
        return connection.model(type)
            .withArguments(getDefaultArguments())
            .addProgressListener(new LoggingProgressListener());
    }

    BuildLauncher buildLauncher(String taskName) {
        return connection.newBuild()
            .forTasks(taskName)
            .withArguments(getDefaultArguments())
            .addProgressListener(new LoggingProgressListener());
    }

    private Iterable<String> getDefaultArguments() {
        String escapedProjectPath = folderConfig.getProjectFolder().toURI().getPath();
        return ImmutableList.of("-PvnRoot=\"" + escapedProjectPath + "\"");
    }

    final class LoggingProgressListener implements ProgressListener {

        @Override
        public void statusChanged(ProgressEvent event) {
            String message = event.getDescription();

            LOG.debug("[gradle] {}", message);

            SwingUtilities.invokeLater(() -> {
                logListeners.forEach(ls -> ls.onLogLine(message));
            });
        }

    }
}
