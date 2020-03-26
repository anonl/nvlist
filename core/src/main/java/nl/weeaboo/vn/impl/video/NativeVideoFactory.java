package nl.weeaboo.vn.impl.video;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.render.IRenderEnv;

/**
 * Default implementation of {@link INativeVideo}.
 */
public final class NativeVideoFactory implements INativeVideoFactory {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    @Override
    public INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv) {

        return new NativeVideo(new GdxVideoPlayerFactory(resourceLoader), filePath, renderEnv);
    }

    /**
     * Returns the set of video file extensions (without the '.' prefix) supported by the video player.
     */
    public static ImmutableSet<String> getSupportedFileExts() {
        return ImmutableSet.of("webm", "ogv");
    }

}
