package nl.weeaboo.vn.buildgui.gradle;

import java.io.File;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

public final class GradleMonitor implements AutoCloseable {

    private ProjectConnection connection;

    public GradleMonitor() {
    }

    public void open(File projectDir) throws CheckedGradleException {
        try {
            connection = GradleConnector.newConnector()
                    .useBuildDistribution()
                    .forProjectDirectory(projectDir)
                    .connect();
        } catch (GradleConnectionException e) {
            throw new CheckedGradleException("Error connecting to Gradle: projectDir=" + projectDir, e);
        }
    }

    @Override
    public void close() {
        connection.close();
    }

    <T> ModelBuilder<T> modelBuilder(Class<T> type) {
        return connection.model(type)
            .addProgressListener(new LoggingProgressListener());
    }

    BuildLauncher buildLauncher(String taskName) {
        return connection.newBuild()
            .forTasks(taskName)
            .addProgressListener(new LoggingProgressListener());
    }

}
