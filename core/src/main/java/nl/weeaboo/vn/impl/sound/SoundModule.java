package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SoundModule.class);

    private final StaticRef<GdxMusicStore> musicStore = StaticEnvironment.MUSIC_STORE;

    protected final IEnvironment env;
    protected final SoundResourceLoader resourceLoader;

    private final ISoundController soundController;

    public SoundModule(IEnvironment env) {
        this(env, new SoundResourceLoader(env), new SoundController());
    }

    public SoundModule(IEnvironment env, SoundResourceLoader resourceLoader,
            ISoundController soundController) {

        this.env = env;
        this.resourceLoader = resourceLoader;

        this.soundController = soundController;
    }

    @Override
    public void destroy() {
        soundController.stopAll();
    }

    @Override
    public void update() {
        soundController.update();
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public @Nullable ISound createSound(SoundType stype, ResourceLoadInfo loadInfo) throws IOException {
        FilePath path = loadInfo.getPath();
        resourceLoader.checkRedundantFileExt(path);

        ResourceId resourceId = resourceLoader.resolveResource(path);
        if (resourceId == null) {
            LOG.debug("Unable to find sound file: " + path);
            return null;
        }

        INativeAudio audio = createNativeAudio(resourceLoader, resourceId);
        if (audio == null) {
            LOG.debug("Unable to create native sound: " + path);
            return null;
        }

        resourceLoader.logLoad(resourceId, loadInfo);
        return new Sound(soundController, stype, resourceId.getFilePath(), audio);
    }

    /**
     * @param filename Path to an audio file
     */
    @Override
    public @Nullable String getDisplayName(FilePath filename) {
        ResourceId resourceId = resourceLoader.resolveResource(filename);
        if (resourceId == null) {
            return null;
        }
        return "";
    }

    private @Nullable INativeAudio createNativeAudio(FileResourceLoader loader, ResourceId resourceId) {
        FilePath filename = resourceId.getFilePath();
        filename = loader.getAbsolutePath(filename);

        IResource<Music> resource = musicStore.get().get(filename);
        if (resource == null) {
            return null;
        }

        return new NativeAudio(resource);
    }

    @Override
    public Collection<FilePath> getSoundFiles(FilePath folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    @Override
    public ISoundController getSoundController() {
        return soundController;
    }

}
