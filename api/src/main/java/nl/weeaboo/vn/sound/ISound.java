package nl.weeaboo.vn.sound;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IStreamingMedia;

public interface ISound extends Serializable, IStreamingMedia {

    /**
     * @throws IOException If an I/O error occurs while attempting to set up a stream from the audio source file to the
     *         audio hardware.
     * @see #start(int)
     */
    void start() throws IOException;

    /**
     * Starts playing the sound.
     *
     * @param loops The number of times the sound should play. Use <code>-1</code> for infinite looping.
     * @throws IOException If an I/O error occurs while attempting to set up a stream from the audio source file to the
     *         audio hardware.
     */
    void start(int loops) throws IOException;

    /**
     * @see #stop(int)
     */
    @Override
    void stop();

    /**
     * Stops playing the sound.
     * @param fadeOutMillis Instead of stopping the sound immediately, fade it
     *        out slowly over the course of <code>fadeOutMillis</code>.
     */
    void stop(int fadeOutMillis);

    /**
     * @return The path of the audio source file.
     */
    FilePath getFilename();

    /**
     * @return The {@link SoundType} for this sound (music, sound effect, voice clip).
     */
    SoundType getSoundType();

    /**
     * @return The number of playback loops remaining, or {@code -1} when looping infinitely.
     * @see #start(int)
     */
    int getLoopsLeft();

    /**
     * Returns the private volume.
     * @see #setPrivateVolume(double)
     */
    double getPrivateVolume();

    /**
     * Returns the master volume.
     * @see #setMasterVolume(double)
     */
    double getMasterVolume();

    /**
     * Returns the final volume used for audio playback. The final volume is determined by multiplying the private and
     * master volume values together.
     */
    double getVolume();

    /**
     * Set the <em>private</em> volume. This is the volume setting intended for use by scripts.
     * @see #getVolume()
     */
    void setPrivateVolume(double v);

    /**
     * Sets the <em>master</em> volume. This is the volume setting controlled by user preferences.
     * @see #getVolume()
     */
    void setMasterVolume(double v);

    /**
     * Sets the audio channel used to play this sound.
     *
     * @see ISoundController#set(int, ISound)
     */
    void setPreferredChannel(int ch);

}
