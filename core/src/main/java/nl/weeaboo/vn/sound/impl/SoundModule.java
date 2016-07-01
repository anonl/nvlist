package nl.weeaboo.vn.sound.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SoundModule.class);

    private final StaticRef<MusicStore> musicStore = StaticEnvironment.MUSIC_STORE;

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
    public ResourceId resolveResource(String filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public ISound createSound(SoundType stype, ResourceLoadInfo loadInfo) throws IOException {
        String filename = loadInfo.getFilename();
        resourceLoader.checkRedundantFileExt(filename);

        ResourceId resourceId = resourceLoader.resolveResource(filename);
        if (resourceId == null) {
            LOG.debug("Unable to find sound file: " + filename);
            return null;
        }

        INativeAudio audio = createNativeAudio(resourceLoader, resourceId);
        if (audio == null) {
            LOG.debug("Unable to find sound file: " + filename);
            return null;
        }

        resourceLoader.logLoad(resourceId, loadInfo);
        return new Sound(soundController, stype, resourceId.getCanonicalFilename(), audio);
    }

    /**
     * @param filename Path to an audio file
     */
    @Override
    public String getDisplayName(String filename) {
        ResourceId resourceId = resourceLoader.resolveResource(filename);
        if (resourceId == null) {
            return null;
        }
        return "";
    }

    private INativeAudio createNativeAudio(FileResourceLoader loader, ResourceId resourceId) {
        String filename = resourceId.getCanonicalFilename();
        filename = loader.getAbsolutePath(filename);

        IResource<Music> resource = musicStore.get().get(filename);
        if (resource == null) {
            return null;
        }

        return new NativeAudio(resource);
    }

    @Override
    public Collection<String> getSoundFiles(String folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    @Override
    public ISoundController getSoundController() {
        return soundController;
    }

}
