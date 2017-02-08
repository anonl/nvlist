package nl.weeaboo.vn.impl.video;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.vn.core.IStreamingMedia;
import nl.weeaboo.vn.render.IRenderEnvConsumer;

public interface INativeVideo extends Serializable, IStreamingMedia, IRenderEnvConsumer {

    /**
     * Preloads some data structures so {@link #play()} needs to do less work. Calling this method entirely optional.
     */
    void prepare();

    /**
     * @return {@code true} if a call to {@link #prepare()} has any use, or {@code false} if prepare-ing further has no
     *         use.
     */
    boolean isPrepared();

    /**
     * Starts video playback.
     *
     * @throws IOException If an I/O error occurs while trying to open the video file for streaming.
     * @throws VideoPlayerInitException If a non-I/O error occurs while trying to setup the video player.
     */
    void play() throws VideoPlayerInitException, IOException;

    /**
     * Renders the current video frame to the screen.
     */
    void render();

    /**
     * Set the audio volume for video playback.
     */
    void setVolume(double volume);

}
