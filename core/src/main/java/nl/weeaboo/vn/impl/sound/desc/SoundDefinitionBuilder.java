package nl.weeaboo.vn.impl.sound.desc;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundDefinitionBuilder implements ISoundDefinition {

    private String filename;
    private @Nullable String displayName;

    public SoundDefinitionBuilder(String filename) {
        this.filename = Checks.checkNotNull(filename);
    }

    public SoundDefinitionBuilder(ISoundDefinition original) {
        this(original.getFilename());

        displayName = original.getDisplayName(); // May be null
    }

    /** Creates a new immutable {@link SoundDefinition} instance. */
    public SoundDefinition build() {
        return new SoundDefinition(this);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * @see #getFilename()
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public @Nullable String getDisplayName() {
        return displayName;
    }

    /**
     * @see #getDisplayName()
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }
}
