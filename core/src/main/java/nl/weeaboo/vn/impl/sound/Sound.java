package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class Sound extends AbstractSound {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final INativeAudio nativeAudio;

    public Sound(ISoundController sctrl, SoundType soundType, FilePath filename, INativeAudio nativeAudio) {
        super(sctrl, soundType, filename);

        this.nativeAudio = Checks.checkNotNull(nativeAudio);
    }

    @Override
    protected void play(int loops) throws IOException {
        nativeAudio.setVolume(getVolume());
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
    protected void onVolumeChanged() {
        super.onVolumeChanged();

        nativeAudio.setVolume(getVolume());
    }

    @Override
    public int getLoopsLeft() {
        return nativeAudio.getLoopsLeft();
    }

}
