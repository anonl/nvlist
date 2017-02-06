package nl.weeaboo.vn.impl.video;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.render.IRenderEnv;

public class NativeVideoFactory implements INativeVideoFactory {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    @Override
    public INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv) {

        return new NativeVideo(new GdxVideoPlayerFactory(resourceLoader), filePath, renderEnv);
    }

}
