package nl.weeaboo.vn.impl.video;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.render.IRenderEnv;

final class NativeVideoFactoryMock implements INativeVideoFactory {

    private static final long serialVersionUID = 1L;

    @Override
    public INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv) {
        return new NativeVideoMock();
    }

}
