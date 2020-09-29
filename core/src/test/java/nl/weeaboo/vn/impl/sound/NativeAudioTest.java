package nl.weeaboo.vn.impl.sound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.gdx.GdxMusicMock;

public class NativeAudioTest {

    private final NativeAudioFactoryMock audioFactory = new NativeAudioFactoryMock();
    private GdxMusicMock gdxMusic = new GdxMusicMock();

    private NativeAudio nativeAudio;

    @Before
    public void before() {
        audioFactory.setNextGdxMusic(gdxMusic);
        nativeAudio = new NativeAudio(audioFactory, FilePath.of("test"));
    }

    @Test
    public void playStop() {
        assertStopped();

        // Play sound with 6 loops
        nativeAudio.play(6);
        Assert.assertEquals(6, nativeAudio.getLoopsLeft());
        assertPlaying();

        nativeAudio.stop();
        assertStopped();
    }

    @Test
    public void pauseResume() {
        nativeAudio.play(1);
        assertPlaying();

        nativeAudio.pause();
        assertPaused();

        nativeAudio.resume();
        assertPlaying();
    }

    @Test
    public void finiteLooping() {
        nativeAudio.play(2);
        assertPlaying();

        // First loop finishes
        gdxMusic.fireComplete();
        Assert.assertEquals(1, nativeAudio.getLoopsLeft());
        assertPlaying();

        // Second (final) loop finished and the sound is stopped
        gdxMusic.fireComplete();
        Assert.assertEquals(0, nativeAudio.getLoopsLeft());
        assertStopped();
    }

    @Test
    public void infiniteLooping() {
        nativeAudio.play(-1);
        assertPlaying();
        Assert.assertEquals(true, gdxMusic.isLooping());

        // No completion listener was registered
        gdxMusic.fireComplete();
        Assert.assertEquals(-1, nativeAudio.getLoopsLeft());

        // Sound just loops forever until stopped manually
        assertPlaying();
        Assert.assertEquals(true, gdxMusic.isLooping());
    }

    @Test
    public void setVolume() {
        assertVolume(1.0);

        // Setting the volume delegates to the internal music object
        nativeAudio.setVolume(0.5);
        assertVolume(0.5);
    }

    /**
     * After deserialization, {@link NativeAudio} should resume playback.
     */
    @Test
    public void testSerialization() {
        nativeAudio.play(1);
        nativeAudio.setVolume(0.5);
        assertPlaying();

        // After serialize -> deserialize the audio is playing again
        reserialize();
        assertPlaying();
        assertVolume(0.5); // Volume is also restored

        // If the audio was paused, it should still be paused after deserialization
        nativeAudio.pause();
        reserialize();
        assertPaused();
        assertVolume(0.5);
    }

    private void reserialize() {
        byte[] serialized = SerializeTester.serializeObject(nativeAudio);
        nativeAudio = SerializeTester.deserializeObject(serialized, NativeAudio.class);
        gdxMusic = (GdxMusicMock)Checks.checkNotNull(nativeAudio.gdxMusic);
    }

    private void assertStopped() {
        Assert.assertEquals(false, nativeAudio.isPlaying());
        Assert.assertEquals(false, gdxMusic.isPlaying());
        Assert.assertEquals(false, nativeAudio.isPaused());
    }

    private void assertPlaying() {
        Assert.assertEquals(true, nativeAudio.isPlaying());
        Assert.assertEquals(true, gdxMusic.isPlaying());
        Assert.assertEquals(false, nativeAudio.isPaused());
    }

    private void assertPaused() {
        Assert.assertEquals(false, nativeAudio.isPlaying());
        Assert.assertEquals(false, gdxMusic.isPlaying());
        Assert.assertEquals(true, nativeAudio.isPaused());
    }

    private void assertVolume(double expected) {
        Assert.assertEquals((float)expected, gdxMusic.getVolume(), 0.001f);
    }

}
