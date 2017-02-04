package nl.weeaboo.vn.render;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.scene.IVisualElement;

public interface IDrawTransform {

    /**
     * @see IVisualElement#getZ()
     */
    short getZ();

    /**
     * Enables/disables clipping. If {@code false}, rendering may occur outside of the paren't bounds.
     */
    boolean isClipEnabled();

    /**
     * Determines how to blend with previously rendered pixels.
     */
    BlendMode getBlendMode();

    /**
     * The transformation matrix used for rendering.
     */
    Matrix getTransform();

}
