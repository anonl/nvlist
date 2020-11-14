package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.annotation.Nullable;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.ResourceId;

final class NativeAudioFactory implements INativeAudioFactory {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final SoundResourceLoader resourceLoader;
    private transient @Nullable GdxMusicStore musicStore;

    public NativeAudioFactory(SoundResourceLoader resourceLoader) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);

        initTransients();
    }

    private void initTransients() {
        musicStore = new GdxMusicStore();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public INativeAudio createNativeAudio(FilePath filePath) {
        return new NativeAudio(this, filePath);
    }

    @Override
    public Music newGdxMusic(FilePath filePath) {
        return musicStore.newGdxMusic(filePath);
    }

    @Override
    public void preloadNormalized(ResourceId resourceId) {
        FilePath absolutePath = resourceLoader.getAbsolutePath(resourceId.getFilePath());
        musicStore.preload(absolutePath);
    }

    @Override
    public void clearCaches() {
        musicStore.clear();
    }

}
