package nl.weeaboo.vn.impl.stats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.Destructibles;
import nl.weeaboo.vn.impl.core.TestEnvironment;

public final class AnalyticsTest {

    private static final ResourceId IMAGE = new ResourceId(MediaType.IMAGE, FilePath.of("image"));
    private static final ResourceId SOUND = new ResourceId(MediaType.SOUND, FilePath.of("sound"));

    private TestEnvironment env;
    private AnalyticsPreloaderStub preloader;
    private Analytics analytics;

    @Before
    public void before() {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();
        preloader = new AnalyticsPreloaderStub();

        analytics = new Analytics(env, preloader);
    }

    @After
    public void after() {
        Destructibles.destroy(env);
    }

    @Test
    public void testSaveLoad() throws IOException {
        logLoad(IMAGE, Arrays.asList("x.lvn:2"));
        logLoad(SOUND, Arrays.asList("x.lvn:3"));

        FilePath savePath = FilePath.of("savePath");
        InMemoryFileSystem fileSystem = new InMemoryFileSystem(false);
        SecureFileWriter sfw = new SecureFileWriter(fileSystem);
        analytics.save(sfw, savePath);
        analytics.load(sfw, savePath);

        // Currently, only images and sounds are preloaded
        assertPreloads("x.lvn:1", IMAGE, SOUND);
    }

    @Test
    public void testPreloadsThrottled() {
        logLoad(IMAGE, Arrays.asList("x.lvn:21"));
        logLoad(SOUND, Arrays.asList("x.lvn:22"));

        // Handle preloads for line 1 (looks 20 lines ahead)
        assertPreloads("x.lvn:1", IMAGE);

        // Because we're often at the same line for several seconds, as an optimization we only look for preloads once
        assertPreloads("x.lvn:1");

        // When the script moves to a new line, we check for preloads again
        assertPreloads("x.lvn:2", IMAGE, SOUND);
    }

    private void assertPreloads(String lvnFileLine, ResourceId... expectedPreloads) {
        analytics.handlePreloads(FileLine.fromString(lvnFileLine));

        List<FilePath> actualPreloads = new ArrayList<>();
        for (MediaType type : MediaType.values()) {
            actualPreloads.addAll(preloader.consumePreloaded(type));
        }
        Assert.assertEquals(Stream.of(expectedPreloads).map(ResourceId::getFilePath).collect(Collectors.toList()),
                actualPreloads);
    }

    private void logLoad(ResourceId id, List<String> stackTrace) {
        analytics.logLoad(id, new ResourceLoadInfo(id.getType(), id.getFilePath(), stackTrace));
    }

}
