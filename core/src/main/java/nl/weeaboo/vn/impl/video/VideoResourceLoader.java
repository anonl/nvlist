package nl.weeaboo.vn.impl.video;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

final class VideoResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    public VideoResourceLoader(IEnvironment env) {
        super(env, MediaType.VIDEO, FilePath.of("video/"));

        setAutoFileExts("webm", "ogv");
    }

}
