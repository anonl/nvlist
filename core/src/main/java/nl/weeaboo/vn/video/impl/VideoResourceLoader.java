package nl.weeaboo.vn.video.impl;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

final class VideoResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    public VideoResourceLoader(IEnvironment env) {
        super(env, MediaType.VIDEO, "video/");

        setAutoFileExts("webm", "ogv");
    }

}
