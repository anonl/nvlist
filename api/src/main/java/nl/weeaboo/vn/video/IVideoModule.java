package nl.weeaboo.vn.video;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.CheckForNull;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.ResourceLoadInfo;

/**
 * Video module
 */
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
    @CheckForNull
    IVideo getBlocking();

    /**
     * Changes the desired video resolution (width x height). Videos are loaded from the resource folder that
     * most closely matches the desired size.
     */
    void setVideoResolution(Dim desiredSize);

    /**
     * Returns the paths for all video files in the specified folder and its sub-folders.
     */
    Collection<FilePath> getVideoFiles(FilePath folder);

}
