package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

public interface IImageEncoder {

    /**
     * Encodes an image file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    EncodedImage encode(ImageWithDef image) throws IOException;

}
