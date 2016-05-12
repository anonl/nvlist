package nl.weeaboo.vn.sound;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.core.ResourceLoadInfo;

public interface ISoundModule extends IModule, IResourceResolver {

    /**
     * Creates a new sound entity that can be used for playing the audio file specified by {@code filename}.
     *
     * @param stype The kind of sound to start playing (voice, sfx, music, ...)
     * @param loadInfo Filename of the requested resource and related metadata.
     *
     * @throws FileNotFoundException If no sound data could be found for the specified filename.
     */
    public ISound createSound(SoundType stype, ResourceLoadInfo loadInfo) throws IOException;

    /**
     * Returns the human-readable name of a sound file.
     *
     * @return The name, or {@code null} if no human-readable name is defined.
     */
    public String getDisplayName(String filename);

    /**
     * Returns the paths for all sound files in the specified folder and its sub-folders.
     */
    public Collection<String> getSoundFiles(String folder);

    /**
     * Returns the sound controller, which provides functions for manipulating audio playback.
     */
    public ISoundController getSoundController();

}
