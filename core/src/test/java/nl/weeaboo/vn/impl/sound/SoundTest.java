package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.SoundType;

public final class SoundTest {

    private static final double VOLUME_EPSILON = 1e-3;
    private static final SoundType SOUND_TYPE = SoundType.SOUND;
    private static final FilePath FILENAME = FilePath.of("myFilename");

    private SoundControllerMock soundController;
    private SoundTestHelper soundTester;
    private NativeAudioMock nativeAudio;
    private Sound sound;

    @Before
    public void before() {
        soundController = new SoundControllerMock();
        soundTester = new SoundTestHelper(soundController);
        nativeAudio = new NativeAudioMock();
        sound = new Sound(soundController, SOUND_TYPE, FILENAME, nativeAudio);
    }

    @Test
    public void testInitialState() {
        Assert.assertEquals(FILENAME, sound.getFilename());
        Assert.assertEquals(SOUND_TYPE, sound.getSoundType());

        assertStopped();
    }

    @Test
    public void playStop() throws IOException {
        assertStopped();

        sound.start();
        Assert.assertEquals(1, sound.getLoopsLeft());
        assertPlaying();

        sound.stop();
        assertStopped();
    }

    @Test
    public void pauseResume() throws IOException {
        sound.start(1);
        assertPlaying();

        sound.pause();
        assertPaused();

        sound.resume();
        assertPlaying();
    }

    @Test
    public void setVolume() {
        assertPrivateVolume(1.0);
        assertMasterVolume(1.0);

        sound.setMasterVolume(.5);
        sound.setMasterVolume(.5);
        assertMasterVolume(.5);

        sound.setPrivateVolume(.25);
        sound.setPrivateVolume(.25);
        assertPrivateVolume(.25);

        // Setting the volume delegates to the internal nativeAudio object
        Assert.assertEquals(0.125, nativeAudio.getVolume(), VOLUME_EPSILON);
    }

    /**
     * Stop sound with gradual fade-out.
     */
    @Test
    public void stopWithFadeOut() throws IOException {
        sound.start();
        sound.stop(2);

        // The sound is still considered to be playing until it finishes fading out
        Assert.assertEquals(1.0, nativeAudio.getVolume(), VOLUME_EPSILON);
        assertPlaying();

        sound.update();
        Assert.assertEquals(0.5, nativeAudio.getVolume(), VOLUME_EPSILON);
        assertPlaying();

        sound.update();
        Assert.assertEquals(0.0, nativeAudio.getVolume(), VOLUME_EPSILON);
        assertPlaying();

        // Fade out completed, sound is stopped
        sound.update();
        assertStopped();
    }

    /**
     * The sound can have a preferred audio channel. If a preferred channel is set, starting the sound will
     * cause it to start playing in that channel, overwriting any sound currently playing in that channel in
     * the sound controller.
     */
    @Test
    public void testPreferredChannel() throws IOException {
        SoundMock otherSound = new SoundMock(soundController, SoundType.VOICE);
        soundController.set(3, otherSound);
        Assert.assertEquals(otherSound, soundController.get(3));

        sound.setPreferredChannel(3);
        sound.start();
        Assert.assertEquals(sound, soundController.get(3));
    }

    private void assertMasterVolume(double expected) {
        Assert.assertEquals(expected, sound.getMasterVolume(), VOLUME_EPSILON);
    }

    private void assertPrivateVolume(double expected) {
        Assert.assertEquals(expected, sound.getPrivateVolume(), VOLUME_EPSILON);
    }

    private void assertStopped() {
        soundTester.assertStopped(sound, nativeAudio);
    }

    private void assertPlaying() {
        soundTester.assertPlaying(sound, nativeAudio);
    }

    private void assertPaused() {
        soundTester.assertPaused(sound, nativeAudio);
    }

}
