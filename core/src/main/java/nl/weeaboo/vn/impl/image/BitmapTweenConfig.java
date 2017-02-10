package nl.weeaboo.vn.impl.image;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.image.IBitmapTweenConfig;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.AlignUtil;

public final class BitmapTweenConfig implements IBitmapTweenConfig {

    private static final long serialVersionUID = 1L;

    private final double duration;
    private final ControlImage controlImage;

    private double range = 0.2;
    private IInterpolator interpolator = Interpolators.SMOOTH;

    private AlignedTexture startTexture = new AlignedTexture();
    private AlignedTexture endTexture = new AlignedTexture();

    /**
     * @param duration The duration (in frames).
     */
    public BitmapTweenConfig(double duration, ControlImage controlImage) {
        this.duration = Checks.checkRange(duration, "duration", 0);
        this.controlImage = Checks.checkNotNull(controlImage);
    }

    /**
     * @see #setRange(double)
     */
    public double getRange() {
        return range;
    }

    @Override
    public void setRange(double range) {
        this.range = Checks.checkRange(range, "range", 0);
    }

    /**
     * @see #setInterpolator(IInterpolator)
     */
    public IInterpolator getInterpolator() {
        return interpolator;
    }

    @Override
    public void setInterpolator(IInterpolator interpolator) {
        this.interpolator = Checks.checkNotNull(interpolator);
    }

    /**
     * @return The duration (in frames).
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @see ControlImage
     */
    public ControlImage getControlImage() {
        return controlImage;
    }

    /**
     * @see #setStartTexture(ITexture, double, double)
     */
    public AlignedTexture getStartTexture() {
        return startTexture;
    }

    @Override
    public void setStartTexture(ITexture texture) {
        setStartTexture(texture, 0, 0);
    }

    @Override
    public void setStartTexture(ITexture texture, double alignX, double alignY) {
        this.startTexture = new AlignedTexture(texture, alignX, alignY);
    }

    /**
     * @see #setEndTexture(ITexture, double, double)
     */
    public AlignedTexture getEndTexture() {
        return endTexture;
    }

    @Override
    public void setEndTexture(ITexture texture) {
        setEndTexture(texture, 0, 0);
    }

    @Override
    public void setEndTexture(ITexture texture, double alignX, double alignY) {
        this.endTexture = new AlignedTexture(texture, alignX, alignY);
    }

    /** Texture that controls the shape of the dissolve and related settings */
    public static class ControlImage implements Serializable {

        private static final long serialVersionUID = 1L;

        private final ITexture texture;
        private final boolean tile;

        public ControlImage(ITexture texture, boolean tile) {
            this.texture = Checks.checkNotNull(texture);
            this.tile = tile;
        }

        /** Returns the texture that determines the shape of the dissolve effect, never {@code null}. */
        public ITexture getTexture() {
            return texture;
        }

        /** If {@code true}, the texture should be tiled rather than stretched. */
        public boolean isTile() {
            return tile;
        }

        /** Returns the relative bounds for the control image based on the bounds of the start/end images. */
        public Rect2D getBounds(Rect2D inputA, Rect2D inputB) {
            if (tile) {
                return AlignUtil.getAlignedBounds(texture, 0, 0);
            } else {
                return Rect2D.combine(inputA, inputB);
            }
        }

        /** Returns the UV-rect for the control image. */
        public Area2D getUV() {
            // TODO: Should this be texture.getUV()?
            return ITexture.DEFAULT_UV;
        }

    }

}
