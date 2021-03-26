package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import org.junit.Assert;

import nl.weeaboo.vn.core.IStreamingMedia;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

final class SoundTestHelper {

    private ISoundController soundController;

    public SoundTestHelper(ISoundController sctrl) {
        this.soundController = sctrl;
    }

    public SoundMock start() {
        return start(SoundType.SOUND);
    }

    public SoundMock start(SoundType stype) {
        SoundMock sound = new SoundMock(soundController, stype);
        try {
            sound.start();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return sound;
    }

    public void assertPlaying(IStreamingMedia... sounds) {
        for (IStreamingMedia sound : sounds) {
            String message = sound.toString();
            Assert.assertEquals(message, true, sound.isPlaying());
            Assert.assertEquals(message, false, sound.isPaused());
            Assert.assertEquals(message, false, sound.isStopped());
        }
    }

    public void assertPaused(IStreamingMedia... sounds) {
        for (IStreamingMedia sound : sounds) {
            String message = sound.toString();
            Assert.assertEquals(message, true, sound.isPlaying());
            Assert.assertEquals(message, true, sound.isPaused());
            Assert.assertEquals(message, false, sound.isStopped());
        }
    }

    public void assertStopped(IStreamingMedia... sounds) {
        for (IStreamingMedia sound : sounds) {
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
