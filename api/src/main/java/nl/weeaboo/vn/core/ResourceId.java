package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;

public final class ResourceId implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String canonicalFilename;

    public ResourceId(String canonicalFilename) {
        this.canonicalFilename = Checks.checkNotNull(canonicalFilename);
    }

    public String getCanonicalFilename() {
        return canonicalFilename;
    }

}
