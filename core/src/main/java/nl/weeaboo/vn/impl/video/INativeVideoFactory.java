package nl.weeaboo.vn.impl.video;

import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

public interface INativeVideoFactory extends Serializable {

    INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv);

}
