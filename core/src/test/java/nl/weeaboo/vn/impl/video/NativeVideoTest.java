package nl.weeaboo.vn.impl.video;

import java.io.IOException;

import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.render.IRenderEnv;

public class NativeVideoTest {

    private final FilePath videoPath = FilePath.of("blank.webm");

    private TestEnvironment env;
    private IRenderEnv renderEnv;
    private NativeVideo nativeVideo;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();
        renderEnv = env.getRenderEnv();

        nativeVideo = new NativeVideo(new MockGdxVideoPlayerFactory(), videoPath, renderEnv);
    }

    @After
    public void after() {
        env.destroy();
    }

    @Test
    public void playStop() throws VideoPlayerInitException, IOException {
        assertStopped();

        nativeVideo.play();
        assertPlaying();

        nativeVideo.pause();
        assertPaused();

        nativeVideo.resume();
        assertPlaying();

        nativeVideo.stop();
        assertStopped();
    }

    @Test
    public void setVolume() {
        // Call prepare() to initialize the inner GDX video player
        nativeVideo.prepare();
        assertVolume(1.0);

        // Setting the volume delegates to the internal videoplayer object
        nativeVideo.setVolume(0.5);
        assertVolume(0.5);
    }

    @Test
    public void testRender() throws VideoPlayerInitException, IOException {
        nativeVideo.play();

        nativeVideo.render();
        // Check that the video size
        Assert.assertEquals(renderEnv.getVirtualSize(), getVideoPlayer().getRenderSize());
    }

    private void assertStopped() {
        Assert.assertEquals(false, nativeVideo.isPlaying());
        Assert.assertEquals(false, nativeVideo.isPaused());
    }

    private void assertPlaying() {
        Assert.assertEquals(true, nativeVideo.isPlaying());
        Assert.assertEquals(false, nativeVideo.isPaused());
    }

    private void assertPaused() {
        // Unlike with music, playing == true while paused
        Assert.assertEquals(true, nativeVideo.isPlaying());
        Assert.assertEquals(true, nativeVideo.isPaused());
    }

    private void assertVolume(double expected) {
        Assert.assertEquals((float)expected, getVideoPlayer().getVolume(), 0.001f);
    }

    private @Nullable MockGdxVideoPlayer getVideoPlayer() {
        return (MockGdxVideoPlayer)nativeVideo.getVideoPlayer();
    }

}
