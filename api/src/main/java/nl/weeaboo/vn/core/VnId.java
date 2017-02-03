package nl.weeaboo.vn.core;

import nl.weeaboo.common.AbstractId;
import nl.weeaboo.common.Checks;

// TODO: Store in NovelPrefs
public final class VnId extends AbstractId {

    private static final long serialVersionUID = 1L;
    private static final String REGEX = "[a-z][a-z0-9_]+";

    public static final VnId UNKNOWN = new VnId("unknown");

    public VnId(String id) {
        super(id);

        Checks.checkArgument(isValidId(id), "ID must be all lowercase alphnumeric (matching " + REGEX + ")");
    }

    /**
     * @return {@code true} if the supplied string can be converted into a valid {@link VnId} instance.
     */
    public static boolean isValidId(String id) {
        return id.matches(REGEX);
    }

}
