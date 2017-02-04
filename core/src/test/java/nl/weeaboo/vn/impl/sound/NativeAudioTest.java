package nl.weeaboo.vn.impl.sound;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.gdx.res.StaticResourceStub;
import nl.weeaboo.vn.impl.core.StaticRef;

public class NativeAudioTest {

    private StaticRef<Music> musicRef = StaticRef.anonymous(Music.class);

    private NativeAudio nativeAudio;
    private MockGdxMusic mockGdxMusic;

    @Before
    public void before() {
        mockGdxMusic = new MockGdxMusic();
        musicRef.set(mockGdxMusic);

        nativeAudio = new NativeAudio(new StaticResourceStub<>(musicRef));
    }

    @After
    public void after() {
        musicRef.set(null);
    }

    @Test
    public void playStop() {
        assertStopped();

        // Play sound with 6 loops
        nativeAudio.play(6);
        Assert.assertEquals(6, nativeAudio.getLoopsLeft());
        assertPlaying();

        nativeAudio.stop(0);
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
        mockGdxMusic.fireComplete();
        Assert.assertEquals(1, nativeAudio.getLoopsLeft());
        assertPlaying();

        // Second (final) loop finished and the sound is stopped
        mockGdxMusic.fireComplete();
        Assert.assertEquals(0, nativeAudio.getLoopsLeft());
        assertStopped();
    }

    @Test
    public void infiniteLooping() {
        nativeAudio.play(-1);
        assertPlaying();
        Assert.assertEquals(true, mockGdxMusic.isLooping());

        // No completion listener was registered
        mockGdxMusic.fireComplete();
        Assert.assertEquals(-1, nativeAudio.getLoopsLeft());

        // Sound just loops forever until stopped manually
        assertPlaying();
        Assert.assertEquals(true, mockGdxMusic.isLooping());
    }

    @Test
    public void setVolume() {
        assertVolume(1.0);

        // Setting the volume delegates to the internal music object
        nativeAudio.setVolume(0.5);
        assertVolume(0.5);
    }

    @Test
    public void musicRefNull() {
        musicRef.set(null);

        /*
         * The internal music reference may become invalid, for example after serialization. This is
         * unrecoverable. Just make sure that no unintended exceptions are thrown when this happens.
         */
        nativeAudio.play(2);
        assertStopped(); // Play failed
        nativeAudio.pause();
        nativeAudio.resume();
        nativeAudio.stop(1_000);
        nativeAudio.setVolume(0.5);
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

        // Reset music object to mimic real deserialization of a save file
        mockGdxMusic.reset();

        nativeAudio = SerializeTester.deserializeObject(serialized, NativeAudio.class);
    }

    private void assertStopped() {
        Assert.assertEquals(false, nativeAudio.isPlaying());
        Assert.assertEquals(false, mockGdxMusic.isPlaying());
        Assert.assertEquals(false, nativeAudio.isPaused());
    }

    private void assertPlaying() {
        Assert.assertEquals(true, nativeAudio.isPlaying());
        Assert.assertEquals(true, mockGdxMusic.isPlaying());
        Assert.assertEquals(false, nativeAudio.isPaused());
    }

    private void assertPaused() {
        Assert.assertEquals(false, nativeAudio.isPlaying());
        Assert.assertEquals(false, mockGdxMusic.isPlaying());
        Assert.assertEquals(true, nativeAudio.isPaused());
    }

    private void assertVolume(double expected) {
        Assert.assertEquals((float)expected, mockGdxMusic.getVolume(), 0.001f);
    }

}
