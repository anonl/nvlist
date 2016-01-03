package nl.weeaboo.vn.sound.impl;

import java.io.IOException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public class Sound implements ISound {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final ISoundController soundController;
    private final SoundType soundType;
    private final String filename;
    private final IAudioAdapter audioAdapter;

    private double privateVolume = 1.0;
    private double masterVolume = 1.0;

    public Sound(ISoundController sctrl, SoundType soundType, String filename,
            IAudioAdapter audioAdapter) {

        this.soundController = Checks.checkNotNull(sctrl);
        this.soundType = Checks.checkNotNull(soundType);
        this.filename = Checks.checkNotNull(filename);
        this.audioAdapter = Checks.checkNotNull(audioAdapter);
    }

    @Override
    public void start() throws IOException {
        start(1);
    }

    @Override
    public void start(int loops) throws IOException {
        audioAdapter.play(loops);

        int channel = soundController.getFreeChannel();
        soundController.set(channel, this);
    }

    @Override
    public void stop() {
        stop(0);
    }

    @Override
    public void stop(int fadeOutMillis) {
        audioAdapter.stop(fadeOutMillis);
    }

    @Override
    public void pause() {
        audioAdapter.pause();
    }

    @Override
    public void resume() {
        audioAdapter.resume();
    }

    @Override
    public boolean isPlaying() {
        return audioAdapter.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return audioAdapter.isPaused();
    }

    protected void onVolumeChanged() {
        audioAdapter.setVolume(getVolume());
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public SoundType getSoundType() {
        return soundType;
    }

    @Override
    public int getLoopsLeft() {
        return audioAdapter.getLoopsLeft();
    }

    @Override
    public boolean isStopped() {
        return !isPlaying() && !isPaused();
    }

    @Override
    public double getVolume() {
        return getPrivateVolume() * getMasterVolume();
    }

    @Override
    public double getPrivateVolume() {
        return privateVolume;
    }

    @Override
    public double getMasterVolume() {
        return masterVolume;
    }

    @Override
    public void setPrivateVolume(double v) {
        if (privateVolume != v) {
            privateVolume = v;

            onVolumeChanged();
        }
    }

    @Override
    public void setMasterVolume(double v) {
        if (masterVolume != v) {
            masterVolume = v;

            onVolumeChanged();
        }
    }

}
