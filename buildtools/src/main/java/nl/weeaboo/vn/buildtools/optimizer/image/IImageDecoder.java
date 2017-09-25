package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

public interface IImageDecoder {

    /**
     * Decodes an image file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    ImageWithDef decode(EncodedImage image) throws IOException;

}
