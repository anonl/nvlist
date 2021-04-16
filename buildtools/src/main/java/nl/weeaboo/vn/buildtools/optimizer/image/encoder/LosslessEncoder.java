package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

/**
 * Only performs lossless optimizations
 */
public class LosslessEncoder implements IImageEncoder {

    private final IPngEncoder pngEncoder;

    public LosslessEncoder() {
        this(new DesktopPngEncoder());
    }

    public LosslessEncoder(IPngEncoder pngEncoder) {
        this.pngEncoder = Checks.checkNotNull(pngEncoder);
    }


    @Override
    public EncodedImage encode(ImageWithDef image) throws IOException {
        return pngEncoder.encode(image);
    }

}
