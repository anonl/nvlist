package nl.weeaboo.vn.impl.sound;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

final class NativeAudioFactory implements INativeAudioFactory {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final StaticRef<GdxMusicStore> musicStore = StaticEnvironment.MUSIC_STORE;

    @Override
    public INativeAudio createNativeAudio(FilePath filePath) throws IOException {
        IResource<Music> resource = musicStore.get().get(filePath);
        if (resource == null) {
            throw new FileNotFoundException("Unable to load resource: " + filePath);
        }
        return new NativeAudio(resource);
    }

}
