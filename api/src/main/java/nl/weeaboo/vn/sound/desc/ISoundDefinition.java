package nl.weeaboo.vn.sound.desc;

import javax.annotation.Nullable;

/**
 * Audio file metadata.
 */
public interface ISoundDefinition {

    /**
     * Name of the file storing sound definitions for audio files in the same folder.
     */
    String SND_DEF_FILE = "snd.json";

    /**
     * The filename of the image (not a file path).
     */
    String getFilename();

    /**
     * @return The display name for this audio file, or {@code null} if no display name was defined.
     */
    @Nullable String getDisplayName();

}
