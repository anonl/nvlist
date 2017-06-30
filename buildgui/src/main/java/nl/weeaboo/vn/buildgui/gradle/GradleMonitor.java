package nl.weeaboo.vn.buildgui.gradle;

import java.io.File;

import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;

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

        GradleProject model = modelBuilder(GradleProject.class).get();
        System.err.printf("%s: %d tasks", model.getName(), model.getTasks().size());
    }

    @Override
    public void close() {
        connection.close();
    }

    private <T> ModelBuilder<T> modelBuilder(Class<T> type) {
        return connection.model(type)
            .addProgressListener(new LoggingProgressListener());
    }

}
