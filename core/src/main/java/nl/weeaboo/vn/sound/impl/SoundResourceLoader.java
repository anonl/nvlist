package nl.weeaboo.vn.sound.impl;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.impl.FileResourceLoader;

final class SoundResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    public SoundResourceLoader(IEnvironment env) {
        super(env, MediaType.SOUND, "snd/");
    }

}
