package nl.weeaboo.vn.sound;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;

public interface ISound extends Serializable {

    /**
     * @see #start(int)
     */
    void start() throws IOException;

    /**
     * Starts playing the sound.
     *
     * @param loops The number of times the sound should play. Use <code>-1</code> for infinite looping.
     */
    void start(int loops) throws IOException;

    /**
     * @see #stop(int)
     */
    void stop();

    /**
     * Stops playing the sound.
     * @param fadeOutMillis Instead of stopping the sound immediately, fade it
     *        out slowly over the course of <code>fadeOutMillis</code>.
     */
    void stop(int fadeOutMillis);

    /**
     * Temporarily pauses playback. Use {@link #resume()} to resume playback.
     */
    void pause();

    /**
     * Resumes a previously paused sound. Behavior is unspecified when the sound
     * is not paused.
     */
    void resume();

    FilePath getFilename();

    SoundType getSoundType();

    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    int getLoopsLeft();

    double getPrivateVolume();
    double getMasterVolume();
    double getVolume();

    void setPrivateVolume(double v);
    void setMasterVolume(double v);
    void setPreferredChannel(int ch);

}
