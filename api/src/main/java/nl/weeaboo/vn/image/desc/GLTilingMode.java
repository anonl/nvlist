package nl.weeaboo.vn.image.desc;

import com.google.common.collect.ImmutableList;

public enum GLTilingMode {
    CLAMP("clamp", 0x812f),   // GL_CLAMP_TO_EDGE
    REPEAT("repeat", 0x2901); // GL_REPEAT

    public static final GLTilingMode DEFAULT = CLAMP;
    public static ImmutableList<GLTilingMode> VALUES = ImmutableList.copyOf(values());

    private final String name;
    private final int glIdentifier;

    private GLTilingMode(String name, int glIdentifier) {
        this.name = name;
        this.glIdentifier = glIdentifier;
    }

    /**
     * Converts a string representation to its corresponding enum value.
     */
    public static GLTilingMode fromString(String s) {
        for (GLTilingMode mode : VALUES) {
            if (s.equalsIgnoreCase(mode.name)) {
                return mode;
            }
        }

        return Boolean.parseBoolean(s) ? GLTilingMode.REPEAT : GLTilingMode.DEFAULT;
    }

    /**
     * @return The OpenGL enum constant for this tiling mode.
     */
    public int getGLIdentifier() {
        return glIdentifier;
    }

    @Override
    public String toString() {
        return name;
    }
}