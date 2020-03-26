package nl.weeaboo.vn.sound;

import java.util.Collection;

import javax.annotation.Nullable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.ResourceLoadInfo;

/**
 * Audio module.
 */
public interface ISoundModule extends IModule, IResourceResolver {

    /**
     * Creates a new sound entity that can be used for playing the audio file specified by {@code filename}.
     *
     * @param stype The kind of sound to start playing (voice, sfx, music, ...)
     * @param loadInfo Filename of the requested resource and related metadata.
     */
    @Nullable ISound createSound(SoundType stype, ResourceLoadInfo loadInfo);

    /**
     * Returns the human-readable name of a sound file.
     *
     * @return The name, or {@code null} if no human-readable name is defined.
     */
    @Nullable String getDisplayName(FilePath filename);

    /**
     * Returns the paths for all sound files in the specified folder and its sub-folders.
     */
    Collection<FilePath> getSoundFiles(FilePath folder);

    /**
     * Returns the sound controller, which provides functions for manipulating audio playback.
     */
    ISoundController getSoundController();

}
