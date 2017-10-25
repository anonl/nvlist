package nl.weeaboo.vn.buildtools.optimizer.image;

public final class ImageResizerConfig {

    private double scaleFactor = 1.0;

    /**
     * The scale factor from source image size to target image size.
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * @see #getScaleFactor()
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

}
