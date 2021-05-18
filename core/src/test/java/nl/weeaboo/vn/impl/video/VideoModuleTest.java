package nl.weeaboo.vn.impl.video;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.video.IVideo;

public class VideoModuleTest {

    private final FilePath blankPath = FilePath.of("blank.webm");

    private VideoModule videoModule;

    @Before
    public void before() {
        HeadlessGdx.init();

        TestEnvironment env = TestEnvironment.newInstance();
        videoModule = new VideoModule(env, new VideoResourceLoader(env), new NativeVideoFactoryMock());
    }

    @Test
    public void validMovie() throws IOException {
        IVideo video = videoModule.movie(new ResourceLoadInfo(MediaType.VIDEO, blankPath));
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
        videoModule.movie(new ResourceLoadInfo(MediaType.VIDEO, blankPath));
        videoModule.movie(new ResourceLoadInfo(MediaType.VIDEO, blankPath));
    }

}
