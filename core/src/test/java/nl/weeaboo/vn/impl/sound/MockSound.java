package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.sound.AbstractSound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class MockSound extends AbstractSound {

    private static final long serialVersionUID = 1L;

    private final MockNativeAudio nativeAudio;

    public MockSound(ISoundController sctrl, SoundType soundType) {
        super(sctrl, soundType, FilePath.of("test.snd"));

        nativeAudio = new MockNativeAudio();
    }

    @Override
    protected void play(int loops) throws IOException {
        nativeAudio.play(loops);
    }

    @Override
    public void stop(int fadeOutMillis) {
        nativeAudio.stop(fadeOutMillis);
    }

    @Override
    public void pause() {
        nativeAudio.pause();
    }

    @Override
    public void resume() {
        nativeAudio.resume();
    }

    @Override
    public boolean isPlaying() {
        return nativeAudio.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return nativeAudio.isPaused();
    }

    @Override
    public int getLoopsLeft() {
        return nativeAudio.getLoopsLeft();
    }

}
