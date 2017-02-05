package nl.weeaboo.vn.sound;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IStreamingMedia;

public interface ISound extends Serializable, IStreamingMedia {

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
    @Override
    void stop();

    /**
     * Stops playing the sound.
     * @param fadeOutMillis Instead of stopping the sound immediately, fade it
     *        out slowly over the course of <code>fadeOutMillis</code>.
     */
    void stop(int fadeOutMillis);

    FilePath getFilename();

    SoundType getSoundType();

    int getLoopsLeft();

    double getPrivateVolume();
    double getMasterVolume();
    double getVolume();

    void setPrivateVolume(double v);
    void setMasterVolume(double v);
    void setPreferredChannel(int ch);

}
