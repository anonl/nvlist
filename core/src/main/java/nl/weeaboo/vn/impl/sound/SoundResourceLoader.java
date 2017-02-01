package nl.weeaboo.vn.impl.sound;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;

final class SoundResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    public SoundResourceLoader(IEnvironment env) {
        super(env, MediaType.SOUND, FilePath.of("snd/"));

        setAutoFileExts("ogg", "mp3");
    }

}
