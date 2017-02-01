package nl.weeaboo.vn.impl.video;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.video.VideoModule;
import nl.weeaboo.vn.impl.video.VideoResourceLoader;
import nl.weeaboo.vn.video.IVideo;

public class VideoModuleTest {

    private final FilePath blankPath = FilePath.of("blank.webm");

    private VideoModule videoModule;

    @Before
    public void before() {
        TestEnvironment env = TestEnvironment.newInstance();
        videoModule = new VideoModule(env, new VideoResourceLoader(env), new MockNativeVideoFactory());
    }

    @Test
    public void validMovie() throws IOException {
        IVideo video = videoModule.movie(new ResourceLoadInfo(blankPath));
        Assert.assertNotNull(video);

        // Video is playing
        Assert.assertSame(video, videoModule.getBlocking());
        Assert.assertEquals(true, video.isPlaying());

        // Stop video
        video.stop();
        Assert.assertEquals(true, video.isStopped());

        Assert.assertEquals(null, videoModule.getBlocking());
    }

    /** Attempt to start a fullscreen movie while a fullscreen movie is still playing. */
    @Test(expected = IllegalStateException.class)
    public void doubleStartMovie() throws IOException {
        videoModule.movie(new ResourceLoadInfo(blankPath));
        videoModule.movie(new ResourceLoadInfo(blankPath));
    }

}
