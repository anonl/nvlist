package nl.weeaboo.vn.impl.video;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.video.INativeVideo;
import nl.weeaboo.vn.impl.video.INativeVideoFactory;
import nl.weeaboo.vn.render.IRenderEnv;

public class MockNativeVideoFactory implements INativeVideoFactory {

    private static final long serialVersionUID = 1L;

    @Override
    public INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv) {
        return new MockNativeVideo();
    }

}
