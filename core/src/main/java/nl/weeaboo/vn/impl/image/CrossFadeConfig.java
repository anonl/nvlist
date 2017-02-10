package nl.weeaboo.vn.impl.image;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.image.ICrossFadeConfig;
import nl.weeaboo.vn.image.ITexture;

public final class CrossFadeConfig implements ICrossFadeConfig {

    private static final long serialVersionUID = 1L;

    private final double duration;

    private IInterpolator interpolator = Interpolators.SMOOTH;

    private AlignedTexture startTexture = new AlignedTexture();
    private AlignedTexture endTexture = new AlignedTexture();

    /**
     * @param duration The duration (in frames).
     */
    public CrossFadeConfig(double duration) {
        this.duration = Checks.checkRange(duration, "duration", 0);
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

    /**
     * @return The bounds to use when rendering.
     */
    public Rect2D getBounds() {
        return Rect2D.combine(startTexture.getBounds(), endTexture.getBounds());
    }

}
