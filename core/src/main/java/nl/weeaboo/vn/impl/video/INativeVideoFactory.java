package nl.weeaboo.vn.impl.video;

import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.render.IRenderEnv;

public interface INativeVideoFactory extends Serializable {

    /** Creates a new {@link INativeVideo} instance. */
    INativeVideo createNativeVideo(FileResourceLoader resourceLoader, FilePath filePath,
            IRenderEnv renderEnv);

}
