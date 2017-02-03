package nl.weeaboo.vn.impl.sound;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class SoundControllerTest {

    private SoundController sc;
    private ExceptionTester exTester;
    private SoundTestHelper testHelper;

    @Before
    public void before() {
        sc = new SoundController();
        exTester = new ExceptionTester();
        testHelper = new SoundTestHelper(sc);
    }

    @Test
    public void pauseResume() {
        MockSound alpha = testHelper.start();
        MockSound beta = testHelper.start();
        testHelper.assertPlaying(alpha, beta);

        // Pause beta manually
        beta.pause();
        testHelper.assertPlaying(alpha);
        testHelper.assertPaused(beta);

        // Pause controller -> all sounds are paused
        sc.setPaused(true);
        testHelper.assertPaused(alpha, beta);

        /*
         * Starting sounds while paused isn't currently supported. If there's a compelling use case to allow
         * it, this might change in the future.
         */
        exTester.expect(IllegalStateException.class, () -> testHelper.start());

        /*
         * Unpause the sound controller. Because only alpha was paused by the controller, only alpha is
         * resumed and beta remains paused.
         */
        sc.setPaused(false);
        testHelper.assertPlaying(alpha);
        testHelper.assertPaused(beta);

        // Unpause beta manually
        beta.resume();
        testHelper.assertPlaying(alpha, beta);
    }

    @Test
    public void stopAll() {
        final MockSound alpha = testHelper.start();

        final MockSound beta = testHelper.start();
        beta.pause();

        sc.setPaused(true); // Pauses alpha
        sc.stopAll();

        // Check that all sounds are stopped
        testHelper.assertStopped(alpha, beta);
        testHelper.assertSoundCount(0);

        // Check that unpausing doesn't accidentally puts stopped sounds back in the paused state
        sc.setPaused(false);
        testHelper.assertStopped(alpha, beta);
    }

    /**
     * The sound controller has a master volume for each sound type. Changes to the master volume are passed
     * to each registered sound of that type.
     */
    @Test
    public void masterVolume() {
        final MockSound sound = testHelper.start(SoundType.SOUND);
        final MockSound music = testHelper.start(SoundType.MUSIC);
        final MockSound voice = testHelper.start(SoundType.VOICE);

        sc.setMasterVolume(SoundType.SOUND, 0.125);
        sc.setMasterVolume(SoundType.MUSIC, 0.250);
        sc.setMasterVolume(SoundType.VOICE, 0.500);

        Assert.assertEquals(0.125, sound.getMasterVolume(), 0.0);
        Assert.assertEquals(0.250, music.getMasterVolume(), 0.0);
        Assert.assertEquals(0.500, voice.getMasterVolume(), 0.0);

        // When a new sound is started, it's also given the current master volume
        MockSound newVoice = testHelper.start(SoundType.VOICE);
        Assert.assertEquals(0.500, newVoice.getMasterVolume(), 0.0);
    }

    /** {@link ISoundController#update()} must be periodically called to clean up stopped sounds. */
    @Test
    public void testUpdate() {
        MockSound alpha = testHelper.start();
        MockSound beta = testHelper.start();

        sc.update();
        testHelper.assertPlaying(alpha, beta);
        testHelper.assertSoundCount(2);

        // Stop beta. In the next update, beta is removed.
        beta.stop();
        sc.update();
        testHelper.assertSoundCount(1);

        // Stopped sounds are removed even when paused
        sc.setPaused(true);
        alpha.stop();
        sc.update();
        testHelper.assertSoundCount(0);

        // Check that unpausing doesn't revive the sound
        sc.setPaused(false);
        testHelper.assertStopped(alpha, beta);
    }

}
