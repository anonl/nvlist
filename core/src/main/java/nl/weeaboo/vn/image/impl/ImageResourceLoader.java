package nl.weeaboo.vn.image.impl;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

final class ImageResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    public ImageResourceLoader(IEnvironment env) {
        super(env, "img/");

        setAutoFileExts("ktx", "png", "jpg");
    }

}
