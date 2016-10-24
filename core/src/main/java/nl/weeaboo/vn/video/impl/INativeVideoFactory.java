package nl.weeaboo.vn.video.impl;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

public interface INativeVideoFactory {

    INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv);

}
