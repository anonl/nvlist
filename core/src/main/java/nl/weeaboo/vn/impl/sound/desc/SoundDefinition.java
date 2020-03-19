package nl.weeaboo.vn.impl.sound.desc;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

/**
 * Default implementation of {@link ISoundDefinition}.
 */
@Immutable
public final class SoundDefinition implements ISoundDefinition {

    // --- Also update SoundDefinitionJson when changing attributes ---
    private final String filename;
    private final @Nullable String displayName; // May be null
    // --- Also update SoundDefinitionJson when changing attributes ---

    /**
     * @see SoundDefinitionBuilder
     */
    public SoundDefinition(String filename) {
        this(new SoundDefinitionBuilder(filename));
    }

    SoundDefinition(ISoundDefinition template) {
        filename = template.getFilename();
        Preconditions.checkArgument(FilePath.of(filename).getName().equals(filename),
                "Filename may not be a path: " + filename);

        displayName = template.getDisplayName();
        if (displayName != null) {
            Checks.checkArgument(displayName.length() > 0,
                    "Display name may be null, but not an empty string");
        }
    }

    /**
     * Instantiates a new {@link SoundDefinition} initialized with the given definition.
     */
    public static SoundDefinition from(ISoundDefinition def) {
        if (def instanceof SoundDefinition) {
            return (SoundDefinition)def;
        }
        return new SoundDefinition(def);
    }

    /**
     * Returns a mutable copy of this definition.
     */
    public SoundDefinitionBuilder builder() {
        return new SoundDefinitionBuilder(this);
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
    public @Nullable String getDisplayName() {
        return displayName;
    }

}
