package nl.weeaboo.vn.render;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;

public interface IDrawTransform {

    short getZ();
    boolean isClipEnabled();
    BlendMode getBlendMode();
    Matrix getTransform();

}
