package nl.weeaboo.vn.impl.sound.desc;

import com.google.common.base.Preconditions;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundDefinition implements ISoundDefinition {

    // --- Also update SoundDefinitionJson when changing attributes ---
    private final String filename;
    private final String displayName; // May be null
    // --- Also update SoundDefinitionJson when changing attributes ---

    /**
     * @param displayName (optional) Display name for this audio file.
     */
    public SoundDefinition(String filename, String displayName) {
        Preconditions.checkArgument(FilePath.of(filename).getName().equals(filename),
                "Filename may not be a path: " + filename);
        this.filename = filename;

        if (displayName != null) {
            Checks.checkArgument(displayName.length() > 0,
                    "Display name may be null, but not an empty string");
        }
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("SoundDesc(%s)", filename);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
