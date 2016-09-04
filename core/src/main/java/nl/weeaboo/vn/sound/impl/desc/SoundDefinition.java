package nl.weeaboo.vn.sound.impl.desc;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundDefinition implements ISoundDefinition {

    // --- Also update SoundDefinitionJson when changing attributes ---
    private final FilePath file;
	private final String displayName; // May be null
    // --- Also update SoundDefinitionJson when changing attributes ---

	/**
	 * @param displayName (optional) Display name for this audio file.
	 */
	public SoundDefinition(FilePath file, String displayName) {
	    this.file = Checks.checkNotNull(file);

	    if (displayName != null) {
	        Checks.checkArgument(displayName.length() > 0,
	                "Display name may be null, but not an empty string");
	    }
	    this.displayName = displayName;
	}

	@Override
	public String toString() {
		return StringUtil.formatRoot("SoundDesc(%s)", file);
	}

	@Override
    public FilePath getFile() {
        return file;
    }

	public boolean hasDisplayName() {
	    return displayName != null;
	}

	/**
	 * @return The display name for this audio file, or {@code null} if no display name was defined.
	 */
	@Override
	public String getDisplayName() {
	    return displayName;
	}

}
