package nl.weeaboo.vn.impl.sound;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.Animation;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

/**
 * Default implementation of {@link ISound}.
 */
public class Sound extends AbstractSound {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final INativeAudio nativeAudio;

    private boolean stopping;
    private transient @Nullable Animation stoppingFadeOut;

    public Sound(ISoundController sctrl, SoundType soundType, FilePath filename, INativeAudio nativeAudio) {
        super(sctrl, soundType, filename);

        this.nativeAudio = Checks.checkNotNull(nativeAudio);
    }

    @Override
    protected void play(int loops) {
        stopping = false;

        nativeAudio.setVolume(getVolume());
        nativeAudio.play(loops);
    }

    @Override
    public void stop(int fadeOutFrames) {
        if (fadeOutFrames > 0) {
            // Fade out slowly over a number of frames
            stopping = true;
            stoppingFadeOut = new Animation(fadeOutFrames);
        } else {
            // Stop immediately
            stopping = false;
            nativeAudio.stop();
        }

        soundController.checkSounds();
    }

    @Override
    public void pause() {
        if (stopping) {
            return;
        }

        nativeAudio.pause();
        soundController.checkSounds();
    }

    @Override
    public void resume() {
        if (stopping) {
            return;
        }

        nativeAudio.resume();
        soundController.checkSounds();
    }

    @Override
    public void update() {
        if (stopping) {
            if (stoppingFadeOut == null || stoppingFadeOut.isFinished()) {
                nativeAudio.stop();
            } else {
                stoppingFadeOut.update();
                onVolumeChanged();
            }
        }
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
    public double getVolume() {
        double volume = super.getVolume();
        if (stopping && stoppingFadeOut != null) {
            volume *= 1.0 - stoppingFadeOut.getNormalizedTime();
        }
        return volume;
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
