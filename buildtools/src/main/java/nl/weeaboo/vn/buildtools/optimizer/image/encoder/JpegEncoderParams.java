package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import nl.weeaboo.common.Checks;

final class JpegEncoderParams {

    private float quality = .9f;

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        Checks.checkRange(quality, "quality", 0.0, 1.0);

        this.quality = quality;
    }

}
