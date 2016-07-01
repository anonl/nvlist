package nl.weeaboo.vn.sound.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.common.Checks;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.io.CustomSerializable;

@CustomSerializable
public class NativeAudio implements INativeAudio {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final IResource<Music> musicRef;

    private boolean paused;

    public NativeAudio(IResource<Music> music) {
        this.musicRef = Checks.checkNotNull(music);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        boolean playing = in.readBoolean();
        int loopsLeft = in.readInt();
        if (playing) {
            doPlay(loopsLeft);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeBoolean(isPlaying());
        out.writeInt(getLoopsLeft());
    }

    @Override
    public void play(int loops) {
        doPlay(loops);
    }

    private void doPlay(int loops) {
        // TODO Implement finite looping

        Music music = musicRef.get();
        if (music != null) {
            music.setLooping(loops < 0 || loops > 1);
            music.play();
            paused = false;
        }
    }

    @Override
    public void pause() {
        Music music = musicRef.get();
        if (music != null) {
            music.pause();
            paused = true;
        }
    }

    @Override
    public void resume() {
        Music music = musicRef.get();
        if (music != null) {
            music.play();
            paused = false;
        }
    }

    @Override
    public void stop(int fadeOutMillis) {
        // TODO Implement fade out time

        Music music = musicRef.get();
        if (music != null) {
            music.stop();
            paused = false;
        }
    }

    @Override
    public boolean isPlaying() {
        Music music = musicRef.get();
        return music != null && music.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public int getLoopsLeft() {
        // TODO Implement looping
        return 0;
    }

    @Override
    public void setVolume(double volume) {
        Music music = musicRef.get();
        if (music != null) {
            music.setVolume((float)volume);
        }
    }

}
