package nl.weeaboo.vn.sound.impl;

import java.io.IOException;
import java.util.Collection;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.core.impl.EntityHelper;
import nl.weeaboo.vn.core.impl.ResourceLoader;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    protected final DefaultEnvironment env;
    protected final ResourceLoader resourceLoader;
    protected final EntityHelper entityHelper;

    private final SoundStore soundStore;
    private final ISoundController soundController;

    public SoundModule(DefaultEnvironment env) {
        this(env, new SoundResourceLoader(env), new SoundStore(), new SoundController());
    }

    public SoundModule(DefaultEnvironment env, ResourceLoader resourceLoader, SoundStore soundStore,
            ISoundController soundController) {

        this.env = env;
        this.resourceLoader = resourceLoader;
        this.entityHelper = new EntityHelper(env.getPartRegistry());

        this.soundStore = soundStore;
        this.soundController = soundController;
    }

    @Override
    public void update() {
        soundController.update();
    }

    @Override
    public Entity createSound(IScreen screen, SoundType stype, ResourceLoadInfo loadInfo) throws IOException {
        // TODO LVN-01 Re-enable analytics, seen log

        Entity e = entityHelper.createScriptableEntity(screen);
        // TODO: Add sound parts
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
