package nl.weeaboo.vn.impl.sound;

import javax.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.AssetManagerResourceStore;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

final class GdxMusicStore extends AssetManagerResourceStore<MusicWrapper> {

    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;

    public GdxMusicStore() {
        super(MusicWrapper.class);

        AssetManager am = assetManager.get();
        am.setLoader(MusicWrapper.class, new MusicWrapperLoader(am.getFileHandleResolver()));
    }

    Music newGdxMusic(FilePath absolutePath) {
        // Use libGDX to load a Music object
        MusicWrapper wrapper = loadResource(absolutePath);

        // Unload the wrapper so we can create more Music instances of the same file in the future
        // Because MusicWrapper isn't Disposable, this doesn't dispose() the wrapped Music instance
        unloadResource(absolutePath);

        // Return the Music which is now no longer associated with AssetManager
        return wrapper.music;
    }


}

/**
 * Non-disposable wrapper type which lets use load multiple Music instances for the same file.
 * AssetManager will only load a single instance of any resource. This is problematic for Music, since you
 * may need multiple instances.
 */
final class MusicWrapper {

    final Music music;

    public MusicWrapper(Music music) {
        this.music = music;
    }

}

final class MusicParameter extends AssetLoaderParameters<MusicWrapper> {
}

final class MusicWrapperLoader extends AsynchronousAssetLoader<MusicWrapper, MusicParameter> {

    private @Nullable Music music;

    public MusicWrapperLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /** Clears internal state associated with a single load operation */
    private void reset() {
        music = null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        music = Gdx.audio.newMusic(file);
    }

    @Override
    public MusicWrapper loadSync(AssetManager manager, String fileName, FileHandle file, MusicParameter parameter) {
        MusicWrapper result = new MusicWrapper(music);
        reset();
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @Nullable Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
            MusicParameter parameter) {
        return null;
    }

}
