package nl.weeaboo.vn.render.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawTransform;

public final class DrawTransform implements IDrawTransform {

    private short z;
    private boolean clipEnabled;
    private BlendMode blendMode;
    private Matrix transform;

    public DrawTransform() {
        clipEnabled = true;
        blendMode = BlendMode.DEFAULT;
        transform = Matrix.identityMatrix();
    }

    public DrawTransform(IDrawTransform other) {
        z = other.getZ();
        clipEnabled = other.isClipEnabled();
        blendMode = other.getBlendMode();
        transform = other.getTransform();
    }

    @Override
    public short getZ() {
        return z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    @Override
    public boolean isClipEnabled() {
        return clipEnabled;
    }

    public void setClipEnabled(boolean clipEnabled) {
        this.clipEnabled = clipEnabled;
    }

    @Override
    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = Checks.checkNotNull(blendMode);
    }

    @Override
    public Matrix getTransform() {
        return transform;
    }

    public void setTransform(Matrix transform) {
        this.transform = Checks.checkNotNull(transform);
    }

}
