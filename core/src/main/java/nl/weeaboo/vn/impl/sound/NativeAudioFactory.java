package nl.weeaboo.vn.impl.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

final class NativeAudioFactory implements INativeAudioFactory {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;

    @Override
    public INativeAudio createNativeAudio(FilePath filePath) {
        return new NativeAudio(this, filePath);
    }

    @Override
    public Music newGdxMusic(FilePath filePath) {
        FileHandleResolver fileResolver = assetManager.get().getFileHandleResolver();
        return Gdx.audio.newMusic(fileResolver.resolve(filePath.toString()));
    }

}
