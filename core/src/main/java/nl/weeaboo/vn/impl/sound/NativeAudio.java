package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.gdx.res.IResource;

@CustomSerializable
public class NativeAudio implements INativeAudio {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(NativeAudio.class);

    private final IResource<Music> musicRef;

    private final AtomicInteger loopsLeft = new AtomicInteger();
    private boolean paused;
    private double volume;

    public NativeAudio(IResource<Music> music) {
        this.musicRef = Checks.checkNotNull(music);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        boolean playing = in.readBoolean();
        int loopsLeft = in.readInt();

        applyVolume();
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
        Music music = musicRef.get();
        if (music != null) {
            if (loops <= 0) {
                music.setOnCompletionListener(null);
            } else {
                // TODO: Doesn't work on Desktop, unlike Android the completion listener is only called
                //       when the music ends, not on every loop.
                music.setOnCompletionListener(new LoopEndListener());
            }

            loopsLeft.set(loops);
            music.setLooping(loops < 0 || loops > 1);
            applyVolume(); // Re-apply volume in case Music object had to be reloaded
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
    public void stop() {
        stop(0);
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
    public boolean isStopped() {
        return !isPlaying() && !isPaused();
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
        return loopsLeft.get();
    }

    @Override
    public void setVolume(double vol) {
        if (volume != vol) {
            volume = vol;

            applyVolume();
        }
    }

    private void applyVolume() {
        Music music = musicRef.get();
        if (music != null) {
            music.setVolume((float)volume);
        }
    }

    private final class LoopEndListener implements Music.OnCompletionListener {

        @Override
        public void onCompletion(Music music) {
            int left = loopsLeft.decrementAndGet();
            if (left <= 0) {
                LOG.debug("Music all loops finished");
                stop(0);
            } else {
                LOG.debug("Decrease music loops -> {}", left);
            }
        }

    }
}
