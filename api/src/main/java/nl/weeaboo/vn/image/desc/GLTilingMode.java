package nl.weeaboo.vn.image.desc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public enum GLTilingMode {
	CLAMP("clamp", 0x812f),   // GL_CLAMP_TO_EDGE
	REPEAT("repeat", 0x2901); // GL_REPEAT

	public static final GLTilingMode DEFAULT = CLAMP;
	public static ImmutableList<GLTilingMode> VALUES = ImmutableList.copyOf(values());

	private static final Logger LOG = LoggerFactory.getLogger(GLTilingMode.class);

	private final String name;
	private final int glIdentifier;

	private GLTilingMode(String name, int glIdentifier) {
		this.name = name;
		this.glIdentifier = glIdentifier;
	}

	public static GLTilingMode fromOrdinal(int ordinal) {
	    if (ordinal >= 0 && ordinal < VALUES.size()) {
	        return VALUES.get(ordinal);
	    } else {
	        LOG.warn("Invalid GLTilingMode ordinal: {}", ordinal);
	        return DEFAULT;
	    }
	}

	public static GLTilingMode fromString(String s) {
		for (GLTilingMode mode : VALUES) {
			if (s.equalsIgnoreCase(mode.name)) {
				return mode;
			}
		}
		return Boolean.parseBoolean(s) ? GLTilingMode.REPEAT : GLTilingMode.DEFAULT;
	}

	public int getGLIdentifier() {
		return glIdentifier;
	}

	@Override
	public String toString() {
		return name;
	}
}