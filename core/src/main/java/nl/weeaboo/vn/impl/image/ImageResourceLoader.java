package nl.weeaboo.vn.impl.image;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

final class ImageResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    public ImageResourceLoader(IEnvironment env) {
        super(env, MediaType.IMAGE);

        setAutoFileExts("ktx", "png", "jpg", "jng");
    }

}
