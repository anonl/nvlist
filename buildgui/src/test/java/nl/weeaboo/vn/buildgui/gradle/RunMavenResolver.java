package nl.weeaboo.vn.buildgui.gradle;

import java.util.Arrays;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
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

final class RunMavenResolver {

    private static final String MAVEN_LAYOUT = "default";

    public static void main(String[] args) throws VersionRangeResolutionException {
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
        serviceLocator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        RepositorySystem repoSystem = serviceLocator.getService(RepositorySystem.class);

        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(session,
                new LocalRepository("build/tmp/repo")));

        RemoteRepository repo = new RemoteRepository.Builder("mavenCentral", MAVEN_LAYOUT,
                "https://repo1.maven.org/maven2/").build();

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(new DefaultArtifact("nl.weeaboo.vn:nvlist-core:[0,)"));
        rangeRequest.setRepositories(Arrays.asList(repo));

        VersionRangeResult result = repoSystem.resolveVersionRange(session, rangeRequest);
        System.out.println(result.getHighestVersion());

        /*
        MavenResolvedArtifact[] resolved = Maven.configureResolver()
            .withRemoteRepo("mavenCentral", "https://repo1.maven.org/maven2/", MAVEN_LAYOUT)
            .resolve("nl.weeaboo.vn:nvlist-desktop:RELEASE")
            .withoutTransitivity()
            .asResolvedArtifact();

        for (MavenResolvedArtifact artifact : resolved) {
            MavenCoordinate coord = artifact.getCoordinate();
            System.out.println(coord);
        }
        */
    }

}
