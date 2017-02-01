package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import com.google.common.base.Preconditions;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public abstract class AbstractSound implements ISound {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final ISoundController soundController;
    private final SoundType soundType;
    private final FilePath filename;

    private double privateVolume = 1.0;
    private double masterVolume = 1.0;
    private int preferredChannel = -1;

    public AbstractSound(ISoundController sctrl, SoundType soundType, FilePath filename) {
        this.soundController = Checks.checkNotNull(sctrl);
        this.soundType = Checks.checkNotNull(soundType);
        this.filename = Checks.checkNotNull(filename);
    }

    @Override
    public final void start() throws IOException {
        start(1);
    }

    @Override
    public final void start(int loops) throws IOException {
        play(loops);

        int channel = preferredChannel;
        if (channel < 0) {
            channel = soundController.getFreeChannel();
        }
        soundController.set(channel, this);
    }

    protected abstract void play(int loops) throws IOException;

    @Override
    public final void stop() {
        stop(0);
    }

    protected void onVolumeChanged() {
    }

    @Override
    public FilePath getFilename() {
        return filename;
    }

    @Override
    public SoundType getSoundType() {
        return soundType;
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

    @Override
    public void setPreferredChannel(int ch) {
        Preconditions.checkArgument(ch == -1
                || (ch >= ISoundController.MIN_CHANNEL && ch <= ISoundController.MAX_CHANNEL));

        preferredChannel = ch;
    }

}
