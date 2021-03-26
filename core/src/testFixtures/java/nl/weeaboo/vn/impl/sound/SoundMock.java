package nl.weeaboo.vn.impl.sound;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class SoundMock extends AbstractSound {

    private static final long serialVersionUID = 1L;

    private final NativeAudioMock nativeAudio;

    public SoundMock(ISoundController sctrl, SoundType soundType) {
        super(sctrl, soundType, FilePath.of("test.snd"));

        nativeAudio = new NativeAudioMock();
    }

    @Override
    protected void play(int loops) {
        nativeAudio.play(loops);
    }

    @Override
    public void stop(int fadeOutFrames) {
        nativeAudio.stop();
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
    public void update() {
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
