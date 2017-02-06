package nl.weeaboo.vn.video;

import java.io.IOException;
import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public interface IVideoModule extends IModule, IResourceResolver {

    /**
     * Starts a full-screen video.
     *
     * @param loadInfo Filename of the requested resource and related metadata.
     * @return An {@link IVideo} object that can be used to control playback.
     * @throws IOException If an I/O error occurs while opening the video file for reading.
     */
    IVideo movie(ResourceLoadInfo loadInfo) throws IOException;

    /**
     * @return The currently playing full-screen video, or {@code null} if there's no full-screen video
     *         currently playing.
     */
    IVideo getBlocking();

    /**
     * Sets the resource folder that videos are loaded from.
     */
    void setVideoFolder(FilePath videoFolder, Dim size);

    /**
     * Returns the paths for all video files in the specified folder and its sub-folders.
     */
    Collection<FilePath> getVideoFiles(FilePath folder);

}
