package nl.weeaboo.vn.sound.desc;

public interface ISoundDefinition {

    /**
     * The filename of the image (not a file path).
     */
    String getFilename();

    /**
     * @return The display name for this audio file, or {@code null} if no display name was defined.
     */
    String getDisplayName();

}
