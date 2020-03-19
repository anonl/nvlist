package nl.weeaboo.vn.impl.sound;

import java.io.Serializable;

import nl.weeaboo.vn.core.IStreamingMedia;
import nl.weeaboo.vn.sound.ISound;

/**
 * Low-level interface for audio.
 *
 * @see ISound
 */
public interface INativeAudio extends Serializable, IStreamingMedia {

    /**
     * Starts audio playback.
     * @param loops The number of times the sound should play. Use <code>-1</code> for infinite looping.
     */
    void play(int loops);

    /**
     * @return The number of playback loops remaining, or {@code -1} when looping infinitely.
     * @see #play(int)
     */
    int getLoopsLeft();

    /**
     * Set the audio volume for playback.
     */
    void setVolume(double volume);

}
