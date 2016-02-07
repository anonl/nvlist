package nl.weeaboo.vn.image.impl;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.RenderUtil;

public class BitmapTweenConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final double duration;
    private final ControlImage controlImage;

    private double range = 0.2;
    private IInterpolator interpolator = Interpolators.SMOOTH;

    private InputTexture startTexture = new InputTexture();
    private InputTexture endTexture = new InputTexture();

    public BitmapTweenConfig(double duration, ControlImage controlImage) {
        this.duration = Checks.checkRange(duration, "duration", 0);
        this.controlImage = Checks.checkNotNull(controlImage);
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = Checks.checkRange(range, "range", 0);
    }

    public IInterpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(IInterpolator interpolator) {
        this.interpolator = Checks.checkNotNull(interpolator);
    }

    public double getDuration() {
        return duration;
    }

    public ControlImage getControlImage() {
        return controlImage;
    }

    public InputTexture getStartTexture() {
        return startTexture;
    }

    public void setStartTexture(ITexture texture) {
        setStartTexture(texture, 0, 0);
    }
    public void setStartTexture(ITexture texture, double alignX, double alignY) {
        this.startTexture = new InputTexture(texture, alignX, alignY);
    }

    public InputTexture getEndTexture() {
        return endTexture;
    }

    public void setEndTexture(ITexture texture) {
        setEndTexture(texture, 0, 0);
    }
    public void setEndTexture(ITexture texture, double alignX, double alignY) {
        this.endTexture = new InputTexture(texture, alignX, alignY);
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

        public ITexture getTexture() {
            return texture;
        }

        public boolean isTile() {
            return tile;
        }

        public Rect2D getBounds(Rect2D inputA, Rect2D inputB) {
            if (tile) {
                return AlignUtil.getAlignedBounds(texture, 0, 0);
            } else {
                return Rect2D.combine(inputA, inputB);
            }
        }

    }

    public static class InputTexture implements Serializable {

        private static final long serialVersionUID = 1L;
        
        /** May be null */
        private final ITexture texture;
        private final double alignX, alignY;

        public InputTexture() {
            this(null, 0, 0);
        }
        public InputTexture(ITexture texture, double alignX, double alignY) {
            this.texture = texture;
            this.alignX = alignX;
            this.alignY = alignY;
        }

        public ITexture getTexture() {
            return texture;
        }

        public Rect2D getBounds() {
            return AlignUtil.getAlignedBounds(texture, alignX, alignY);
        }

        public Area2D getUV(Area2D baseUV) {
            return RenderUtil.combineUV(baseUV, texture != null ? texture.getUV() : ITexture.DEFAULT_UV);
        }

    }

}
