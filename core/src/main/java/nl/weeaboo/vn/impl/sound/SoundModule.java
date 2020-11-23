package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.AbstractModule;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

/**
 * Sub-module for audio.
 */
public final class SoundModule extends AbstractModule implements ISoundModule {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SoundModule.class);

    private final SoundResourceLoader resourceLoader;
    private final ISoundController soundController;
    private final INativeAudioFactory nativeAudioFactory;

    public SoundModule(IEnvironment env) {
        this(new SoundResourceLoader(env), new SoundController());
    }

    public SoundModule(SoundResourceLoader resourceLoader, ISoundController soundController) {
        this.resourceLoader = resourceLoader;
        this.soundController = soundController;

        nativeAudioFactory = new NativeAudioFactory(resourceLoader);
        resourceLoader.setPreloadHandler(nativeAudioFactory);
    }

    /**
     * Returns the set of file extensions (without the '.' prefix) supported by the load methods of this
     * class.
     */
    public static ImmutableSet<String> getSupportedFileExts() {
        return ImmutableSet.of("ogg", "mp3");
    }

    @Override
    public void destroy() {
        super.destroy();

        soundController.stopAll();
    }

    @Override
    public void update() {
        super.update();

        soundController.update();
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public @Nullable ISound createSound(SoundType stype, ResourceLoadInfo loadInfo) {
        FilePath path = loadInfo.getPath();
        resourceLoader.checkRedundantFileExt(path);

        ResourceId resourceId = resolveResource(path);
        if (resourceId == null) {
            LOG.debug("Unable to find audio file: {}", path);
            return null;
        }

        FilePath absolutePath = resourceLoader.getAbsolutePath(resourceId.getFilePath());
        INativeAudio nativeAudio;
        try {
            nativeAudio = nativeAudioFactory.createNativeAudio(absolutePath);
        } catch (IOException e) {
            LOG.warn("Error loading audio file: {}", path);
            return null;
        }

        resourceLoader.logLoad(resourceId, loadInfo);
        return new Sound(soundController, stype, resourceId.getFilePath(), nativeAudio);
    }

    /**
     * @param filename Path to an audio file
     */
    @Override
    public @Nullable String getDisplayName(FilePath filename) {
        ResourceId resourceId = resolveResource(filename);
        if (resourceId == null) {
            return null;
        }

        ISoundDefinition def = resourceLoader.getSoundDef(resourceId.getFilePath());
        if (def == null) {
            return null;
        }

        return def.getDisplayName();
    }

    @Override
    public Collection<FilePath> getSoundFiles(FilePath folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    @Override
    public ISoundController getSoundController() {
        return soundController;
    }

    @Override
    public void preload(FilePath path) {
        resourceLoader.preload(path);
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
        super.onPrefsChanged(config);

        soundController.setMasterVolume(SoundType.MUSIC, config.get(NovelPrefs.MUSIC_VOLUME));
        soundController.setMasterVolume(SoundType.SOUND, config.get(NovelPrefs.SOUND_EFFECT_VOLUME));
        soundController.setMasterVolume(SoundType.VOICE, config.get(NovelPrefs.VOICE_VOLUME));
    }

    @Override
    public void clearCaches() {
        super.clearCaches();

        nativeAudioFactory.clearCaches();
    }

}
