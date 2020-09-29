package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.audio.Music;
import com.google.common.annotations.VisibleForTesting;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.gdx.res.GdxCleaner;

/**
 * Default implementation of {@link INativeAudio}.
 */
@CustomSerializable
class NativeAudio implements INativeAudio {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(NativeAudio.class);

    private final INativeAudioFactory audioFactory;
    private final FilePath filePath;

    @VisibleForTesting
    transient @Nullable Music gdxMusic;

    private final AtomicInteger loopsLeft = new AtomicInteger();
    private boolean paused;
    private boolean playing;
    private double volume;

    public NativeAudio(INativeAudioFactory audioFactory, FilePath absolutePath) {
        this.audioFactory = audioFactory;
        this.filePath = absolutePath;

        gdxMusic = initMusic();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        gdxMusic = initMusic();
        applyVolume();
        if (playing && !paused) {
            doPlay(loopsLeft.get());
        }
    }

    private Music initMusic() {
        Checks.checkState(gdxMusic == null, "Accidental overwrite of gdxMusic");

        Music m = audioFactory.newGdxMusic(filePath);
        /*
         * Dispose GDX music object when NativeAudio is garbage collected to prevent a memory leak if stop()
         * is never called.
         */
        GdxCleaner.get().register(this, m);
        gdxMusic = m;
        return m;
    }

    @Override
    public void play(int loops) {
        doPlay(loops);
    }

    private void doPlay(int loops) {
        Music m = gdxMusic;
        if (m == null) {
            m = initMusic();
        }

        if (loops <= 0) {
            m.setOnCompletionListener(null);
        } else {
            // TODO: Doesn't work on Desktop, unlike Android the completion listener is only called
            //       when the music ends, not on every loop.
            m.setOnCompletionListener(new LoopEndListener());
        }

        loopsLeft.set(loops);
        m.setLooping(loops < 0 || loops > 1);
        applyVolume(); // Re-apply volume in case Music object had to be reloaded

        paused = false;
        playing = true;
        m.play();
    }

    @Override
    public void pause() {
        Music m = gdxMusic;
        if (m != null) {
            m.pause();
            paused = true;
        }
    }

    @Override
    public void resume() {
        Music m = gdxMusic;
        if (m != null) {
            m.play();
            paused = false;
        }
    }

    @Override
    public void stop() {
        Music m = gdxMusic;
        if (m != null) {
            paused = false;
            playing = false;

            m.stop();
        }
    }

    @Override
    public boolean isStopped() {
        return !playing && !paused;
    }

    @Override
    public boolean isPlaying() {
        return playing && !paused;
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
        Music m = gdxMusic;
        if (m != null) {
            m.setVolume((float)volume);
        }
    }

    private final class LoopEndListener implements Music.OnCompletionListener {

        @Override
        public void onCompletion(Music music) {
            int left = loopsLeft.decrementAndGet();
            if (left <= 0) {
                LOG.debug("Audio all loops finished");
                stop();
            } else {
                LOG.debug("Decrease music loops -> {}", left);
            }
        }

    }
}
