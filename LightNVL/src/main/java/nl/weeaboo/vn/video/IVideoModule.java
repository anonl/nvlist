package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.common.Dim;

public interface IVideoModule extends Serializable {

    /**
     * Starts a full-screen video.
     *
     * @param filename Path to the video file that should be played.
     * @return An {@link IVideo} object that can be used to control playback.
     */
    IVideo movie(String filename) throws IOException;

    /**
     * @return The currently playing full-screen video, or {@code null} if there's no full-screen video
     *         currently playing.
     */
    IVideo getBlocking();

    /**
     * Sets the resource folder that videos are loaded from.
     */
    void setVideoFolder(String videoFolder, Dim size);

    /**
     * Returns the paths for all video files in the specified folder and its sub-folders.
     */
    Collection<String> getVideoFiles(String folder);

}
