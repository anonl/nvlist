package nl.weeaboo.vn.image.impl;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

final class ImageResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    public ImageResourceLoader(IEnvironment env) {
        super(env, MediaType.IMAGE, FilePath.of("img/"));

        setAutoFileExts("ktx", "png", "jpg", "jng");
    }

}
