package nl.weeaboo.vn.video.impl;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

public class MockNativeVideoFactory implements INativeVideoFactory {

    @Override
    public INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv) {
        return new MockNativeVideo(filePath);
    }

}
