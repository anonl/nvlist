package nl.weeaboo.vn.image.desc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public enum GLScaleFilter {
    NEAREST("nearest", 0x2600, false),
    LINEAR("linear", 0x2601, false),
    NEAREST_MIPMAP("nearest mipmap", 0x2702, true),
    LINEAR_MIPMAP("linear mipmap", 0x2703, true);

    public static final GLScaleFilter DEFAULT = LINEAR;
    public static final ImmutableList<GLScaleFilter> VALUES = ImmutableList.copyOf(values());

    private static final Logger LOG = LoggerFactory.getLogger(GLScaleFilter.class);

    private final String name;
    private final int glIdentifier;
    private final boolean isMipmap;

    private GLScaleFilter(String name, int glIdentifier, boolean isMipmap) {
        this.name = name;
        this.glIdentifier = glIdentifier;
        this.isMipmap = isMipmap;
    }

    /**
     * Converts a string representation to its corresponding enum value.
     */
    public static GLScaleFilter fromString(String s) {
        for (GLScaleFilter filter : VALUES) {
            if (s.equalsIgnoreCase(filter.name)) {
                return filter;
            }
        }

        LOG.warn("Invalid GLScaleFilter string: {}", s);
        return GLScaleFilter.DEFAULT;
    }

    /**
     * @return The OpenGL enum constant for this filter.
     */
    public int getGLIdentifier() {
        return glIdentifier;
    }

    /**
     * @return {@code true} if this filter uses mipmaps.
     */
    public boolean isMipmap() {
        return isMipmap;
    }

    @Override
    public String toString() {
        return name;
    }
}