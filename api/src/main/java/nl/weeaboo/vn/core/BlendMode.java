package nl.weeaboo.vn.core;

/**
 * The blend mode determines how new pixels are combined with existing pixels during rendering.
 */
public enum BlendMode {

    /**
     * Default blend mode, rendering with transparency.
     */
    DEFAULT,

    /**
     * Renders without transparency.
     */
    OPAQUE,

    /**
     * Additive blending, commonly used to create glow effects.
     */
    ADD;

}
