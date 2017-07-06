package nl.weeaboo.vn.impl.sound;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

final class SoundResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    public SoundResourceLoader(IEnvironment env) {
        super(env, MediaType.SOUND);

        setAutoFileExts("ogg", "mp3");
    }

}
