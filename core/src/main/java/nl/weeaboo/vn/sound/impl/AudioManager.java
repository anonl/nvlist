package nl.weeaboo.vn.sound.impl;

import java.io.Serializable;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;

final class AudioManager implements Serializable {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final StaticRef<MusicStore> musicStore = StaticEnvironment.MUSIC_STORE;

    /**
     * @param filename Path to an audio file
     */
    public String getDisplayName(String filename) {
        return "";
    }

    public IAudioAdapter getMusic(FileResourceLoader loader, String filename) {
        filename = loader.getAbsolutePath(filename);

        IResource<Music> resource = musicStore.get().get(filename);
        if (resource == null) {
            return null;
        }

        return new MusicAudioAdapter(resource);
    }
}
