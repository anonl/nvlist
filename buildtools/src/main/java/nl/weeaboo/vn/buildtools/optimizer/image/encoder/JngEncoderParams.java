package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import nl.weeaboo.common.Checks;

final class JngEncoderParams {

    private float jpegQuality = .90f;
    private float jpegAlphaQuality = .95f;
    private boolean allowLossyAlpha = true;

    public float getJpegQuality() {
        return jpegQuality;
    }

    public void setJpegQuality(float jpegQuality) {
        Checks.checkRange(jpegQuality, "jpegQuality", 0, 1);

        this.jpegQuality = jpegQuality;
    }

    public float getJpegAlphaQuality() {
        return jpegAlphaQuality;
    }

    public void setJpegAlphaQuality(float jpegAlphaQuality) {
        Checks.checkRange(jpegAlphaQuality, "jpegAlphaQuality", 0, 1);

        this.jpegAlphaQuality = jpegAlphaQuality;
    }

    public boolean isAllowLossyAlpha() {
        return allowLossyAlpha;
    }

    public void setAllowLossyAlpha(boolean allowLossyAlpha) {
        this.allowLossyAlpha = allowLossyAlpha;
    }

}
