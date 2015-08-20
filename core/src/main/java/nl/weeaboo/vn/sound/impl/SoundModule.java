package nl.weeaboo.vn.sound.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.core.impl.EntityHelper;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SoundModule.class);

    protected final DefaultEnvironment env;
    protected final SoundResourceLoader resourceLoader;
    protected final EntityHelper entityHelper;

    private final AudioManager soundStore;
    private final ISoundController soundController;

    public SoundModule(DefaultEnvironment env) {
        this(env, new SoundResourceLoader(env), new AudioManager(), new SoundController());
    }

    public SoundModule(DefaultEnvironment env, SoundResourceLoader resourceLoader, AudioManager soundStore,
            ISoundController soundController) {

        this.env = env;
        this.resourceLoader = resourceLoader;
        this.entityHelper = new EntityHelper(env.getPartRegistry());

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
    public Entity createSound(IScreen screen, SoundType stype, ResourceLoadInfo loadInfo) throws IOException {
        String filename = loadInfo.getFilename();
        resourceLoader.checkRedundantFileExt(filename);

        String normalized = resourceLoader.normalizeFilename(filename);
        if (normalized == null) {
            LOG.debug("Unable to find sound file: " + filename);
            return null;
        }
        resourceLoader.logLoad(loadInfo);

        IAudioAdapter audio = soundStore.getMusic(resourceLoader, normalized);

        Entity e = entityHelper.createScriptableEntity(screen);
        entityHelper.addSoundPart(e, soundController, stype, normalized, audio);
        return e;
    }

    @Override
    public String getDisplayName(String filename) {
        String normalizedFilename = resourceLoader.normalizeFilename(filename);
        if (normalizedFilename == null) {
            return null;
        }
        return soundStore.getDisplayName(normalizedFilename);
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
