package nl.weeaboo.vn.impl.text;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

final class FontResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = TextImpl.serialVersionUID;

    public FontResourceLoader(IEnvironment env) {
        super(env, MediaType.FONT);

        setAutoFileExts("ttf");
    }

}
