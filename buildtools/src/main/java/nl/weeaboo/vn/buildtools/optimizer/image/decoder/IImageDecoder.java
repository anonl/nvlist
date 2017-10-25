package nl.weeaboo.vn.buildtools.optimizer.image.decoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

/**
 * Decodes an image
 */
public interface IImageDecoder {

    /**
     * Decodes an image file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    ImageWithDef decode(EncodedImage image) throws IOException;

}
