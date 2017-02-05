package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import org.junit.Assert;

import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

final class SoundTestHelper {

    private ISoundController soundController;

    public SoundTestHelper(ISoundController sctrl) {
        this.soundController = sctrl;
    }

    public MockSound start() {
        return start(SoundType.SOUND);
    }

    public MockSound start(SoundType stype) {
        MockSound sound = new MockSound(soundController, stype);
        try {
            sound.start();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return sound;
    }

    public void assertPlaying(ISound... sounds) {
        for (ISound sound : sounds) {
            String message = sound.toString();
            Assert.assertEquals(message, true, sound.isPlaying());
            Assert.assertEquals(message, false, sound.isPaused());
            Assert.assertEquals(message, false, sound.isStopped());
        }
    }

    public void assertPaused(ISound... sounds) {
        for (ISound sound : sounds) {
            String message = sound.toString();
            Assert.assertEquals(message, true, sound.isPlaying());
            Assert.assertEquals(message, true, sound.isPaused());
            Assert.assertEquals(message, false, sound.isStopped());
        }
    }

    public void assertStopped(ISound... sounds) {
        for (ISound sound : sounds) {
            String message = sound.toString();
            Assert.assertEquals(message, false, sound.isPlaying());
            Assert.assertEquals(message, false, sound.isPaused());
            Assert.assertEquals(message, true, sound.isStopped());
        }
    }

    public void assertSoundCount(int expected) {
        int count = 0;
        for (int n = ISoundController.MIN_CHANNEL; n <= ISoundController.MAX_CHANNEL; n++) {
            if (soundController.get(n) != null) {
                count++;
            }
        }
        Assert.assertEquals(expected, count);
    }

}
