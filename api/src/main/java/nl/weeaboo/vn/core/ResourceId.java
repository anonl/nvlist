package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;

public final class ResourceId implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MediaType type;
    private final String canonicalFilename;

    public ResourceId(MediaType type, String canonicalFilename) {
        this.type = Checks.checkNotNull(type);
        this.canonicalFilename = Checks.checkNotNull(canonicalFilename);
    }

    @Override
    public String toString() {
        return canonicalFilename;
    }

    public MediaType getType() {
        return type;
    }

    public String getCanonicalFilename() {
        return canonicalFilename;
    }

}
