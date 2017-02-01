package nl.weeaboo.vn.impl.video;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.impl.video.Video;

public class VideoTest {

    private static final FilePath VIDEO_FILE = FilePath.of("test.webm");

    private MockNativeVideo nativeVideo;
    private Video video;

    @Before
    public void before() {
        nativeVideo = new MockNativeVideo();
        video = new Video(VIDEO_FILE, nativeVideo);

        Assert.assertEquals(VIDEO_FILE, video.getFilename());
    }

    @Test
    public void playPauseStop() throws IOException {
        assertStopped();
        video.start();
        assertPlaying();
        video.pause();
        assertPaused();
        video.resume();
        assertPlaying();
        video.stop();
        assertStopped();
    }

    @Test
    public void applyVolume() {
        assertVolume(1.0);
        video.setMasterVolume(0.5);
        assertVolume(0.5);
        video.setPrivateVolume(0.25);
        assertVolume(0.125);
    }

    @Test
    public void delegatePrepare() throws IOException {
        assertPrepared(false);
        video.prepare();
        assertPrepared(true);
    }

    @Test
    public void delegateRender() {
        // setRenderEnv() delegates to the embedded native video
        Assert.assertEquals(null, nativeVideo.getRenderEnv());
        video.setRenderEnv(CoreTestUtil.BASIC_ENV);
        Assert.assertEquals(CoreTestUtil.BASIC_ENV, nativeVideo.getRenderEnv());

        // render() delegated to the embedded native video
        Assert.assertEquals(0, nativeVideo.consumeRenderCount());
        video.render();
        Assert.assertEquals(1, nativeVideo.consumeRenderCount());
    }

    private void assertPrepared(boolean expected) {
        Assert.assertEquals(expected, video.isPrepared());
        Assert.assertEquals(expected, nativeVideo.isPrepared());
    }

    private void assertVolume(double expectedVolume) {
        Assert.assertEquals(expectedVolume, video.getVolume(), 0.0);
        Assert.assertEquals(expectedVolume, nativeVideo.getVolume(), 0.0);
    }

    private void assertStopped() {
        Assert.assertEquals(false, video.isPlaying());
        Assert.assertEquals(false, video.isPaused());
        Assert.assertEquals(true, video.isStopped());
    }

    private void assertPaused() {
        Assert.assertEquals(true, video.isPlaying());
        Assert.assertEquals(true, video.isPaused());
        Assert.assertEquals(false, video.isStopped());
    }

    private void assertPlaying() {
        Assert.assertEquals(true, video.isPlaying());
        Assert.assertEquals(false, video.isPaused());
        Assert.assertEquals(false, video.isStopped());
    }

}
