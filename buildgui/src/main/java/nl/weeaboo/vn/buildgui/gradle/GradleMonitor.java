package nl.weeaboo.vn.buildgui.gradle;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

public final class GradleMonitor implements AutoCloseable {

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

    <T> ModelBuilder<T> modelBuilder(Class<T> type) {
        return connection.model(type)
            .withArguments(getDefaultArguments());
    }

    BuildLauncher buildLauncher(String taskName) {
        return connection.newBuild()
            .forTasks(taskName)
            .withArguments(getDefaultArguments());
    }

    private Iterable<String> getDefaultArguments() {
        String escapedProjectPath = folderConfig.getProjectFolder().toURI().getPath();
        return ImmutableList.of("-PvnRoot=\"" + escapedProjectPath + "\"");
    }

}
