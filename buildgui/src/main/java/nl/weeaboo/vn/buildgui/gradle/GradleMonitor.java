package nl.weeaboo.vn.buildgui.gradle;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

public final class GradleMonitor implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GradleMonitor.class);

    static {
        Runtime.getRuntime()
                .addShutdownHook(new Thread("GradleMonitor-ShutdownHook") {
                    @Override
                    public void run() {
                        /*
                         * There's unfortunately no way to stop spawned daemons in the public API, so use this
                         * internal API instead...
                         */
                        DefaultGradleConnector.close();
                    }
                });
    }

    private ProjectConnection connection;
    private ProjectFolderConfig folderConfig;

    public GradleMonitor() {
    }

    /**
     * Connects to the NVList project at the specified location.
     *
     * @throws CheckedGradleException If connecting the the project's Gradle build fails.
     */
    public void open(ProjectFolderConfig folderConfig) throws CheckedGradleException {
        if (connection != null) {
            LOG.warn("Double-open of Gradle monitor detected: folderConfig={}", folderConfig);
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
        connection = null;
    }

    <T> ModelBuilder<T> modelBuilder(Class<T> type) {
        return connection.model(type)
                .setStandardOutput(System.out)
                .setStandardError(System.err)
                .withArguments(getDefaultArguments());
    }

    BuildLauncher buildLauncher(String taskName) {
        return connection.newBuild()
            .forTasks(taskName)
            .setStandardOutput(System.out)
            .setStandardError(System.err)
            .withArguments(getDefaultArguments());
    }

    private Iterable<String> getDefaultArguments() {
        String projectPath = ProjectFolderConfig.toCanonicalPath(folderConfig.getProjectFolder());
        for (String c : new String[] {" ", "\""}) {
            if (projectPath.contains(c)) {
                LOG.warn("Project path contains a dangerous character ({}), things may break: {}",
                        c, projectPath);
            }
        }

        ImmutableList<String> result = ImmutableList.of("-PvnRoot=" + projectPath);
        LOG.debug("Default Gradle arguments: {}", result);
        return result;
    }

}
