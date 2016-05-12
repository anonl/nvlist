package nl.weeaboo.vn.sound.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SoundModule.class);

    protected final DefaultEnvironment env;
    protected final SoundResourceLoader resourceLoader;

    private final AudioManager soundStore;
    private final ISoundController soundController;

    public SoundModule(DefaultEnvironment env) {
        this(env, new SoundResourceLoader(env), new AudioManager(), new SoundController());
    }

    public SoundModule(DefaultEnvironment env, SoundResourceLoader resourceLoader, AudioManager soundStore,
            ISoundController soundController) {

        this.env = env;
        this.resourceLoader = resourceLoader;

        this.soundStore = soundStore;
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
        resourceLoader.logLoad(resourceId, loadInfo);

        IAudioAdapter audio = soundStore.getMusic(resourceLoader, resourceId.getCanonicalFilename());

        return new Sound(soundController, stype, resourceId.getCanonicalFilename(), audio);
    }

    @Override
    public String getDisplayName(String filename) {
        ResourceId resourceId = resourceLoader.resolveResource(filename);
        if (resourceId == null) {
            return null;
        }
        return soundStore.getDisplayName(resourceId.getCanonicalFilename());
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
