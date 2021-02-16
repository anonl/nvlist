package nl.weeaboo.vn.buildgui.gradle;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildtools.task.AbstractTask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

/**
 * Checks for updates to NVList.
 */
public final class CheckForUpdatesTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory.getLogger(CheckForUpdatesTask.class);
    private static final String MAVEN_LAYOUT = "default";

    private final String nvlistVersion;
    private final CopyOnWriteArrayList<IBuildLogListener> logListeners;

    public CheckForUpdatesTask(String nvlistVersion, CopyOnWriteArrayList<IBuildLogListener> logListeners) {
        this.nvlistVersion = Objects.requireNonNull(nvlistVersion);
        this.logListeners = new CopyOnWriteArrayList<>(logListeners);
    }

    /**
     * Starts the check-for-updates task. When the task finishes, the
     * {@link #fireFinished(TaskResultType, String)} method is called.
     */
    public void start() {
        runInBackground();
    }

    private void runInBackground() {
        String message;
        try {
            final RepositorySystem repoSystem = createRepoSystem();
            final RepositorySystemSession session = startSession(repoSystem);

            RemoteRepository repo = new RemoteRepository.Builder("mavenCentral", MAVEN_LAYOUT,
                    "https://repo1.maven.org/maven2/").build();

            VersionRangeRequest rangeRequest = new VersionRangeRequest();
            rangeRequest.setArtifact(new DefaultArtifact("nl.weeaboo.vn:nvlist-core:[0,)"));
            rangeRequest.setRepositories(Arrays.asList(repo));

            VersionRangeResult result = repoSystem.resolveVersionRange(session, rangeRequest);
            LOG.debug("Version request finished: {}", result);

            message = "Current NVList version: " + nvlistVersion
                    + "\nHighest available NVList version: " + result.getHighestVersion();
            fireLogLine(message, LogStyles.INFO_COLOR);
            fireFinished(TaskResultType.SUCCESS, message);
        } catch (VersionRangeResolutionException | RuntimeException e) {
            LOG.warn("Error in check-for-updates task", e);

            message = e.toString();
            fireLogLine(message, LogStyles.ERROR_COLOR);
            fireFinished(TaskResultType.FAILED, message);
        }

        showResultMessage(message);
    }

    private void showResultMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Check for update",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private RepositorySystemSession startSession(RepositorySystem repoSystem) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(session,
                new LocalRepository("build/tmp/repo")));
        return session;
    }

    private RepositorySystem createRepoSystem() {
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
        serviceLocator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        RepositorySystem repoSystem = serviceLocator.getService(RepositorySystem.class);
        return repoSystem;
    }

    private void fireLogLine(String line, Color color) {
        logListeners.forEach(ls -> ls.onLogLine(line, color));
    }

}
