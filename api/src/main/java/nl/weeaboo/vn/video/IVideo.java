package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IStreamingMedia;
import nl.weeaboo.vn.render.IRenderEnvConsumer;

public interface IVideo extends Serializable, IStreamingMedia, IRenderEnvConsumer {

    /**
     * Preloads some data structures so {@link #start()} needs to do less work. Calling this method entirely optional.
     * @throws IOException If an I/O error occurs.
     */
    void prepare() throws IOException;

    /**
     * @return {@code true} if a call to {@link #prepare()} has any use, or {@code false} if prepare-ing further has no
     *         use.
     */
    boolean isPrepared();

    /**
     * Starts video playback.
     * @throws IOException If an I/O error occurs while trying to open the video file for streaming.
     */
    void start() throws IOException;

    /**
     * Renders the current video frame to the screen.
     */
    void render();

    /**
     * @return The path of the video source file.
     */
    FilePath getFilename();

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

}
