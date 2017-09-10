package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.File;
import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

public interface IImageEncoder {

    /**
     * Writes an image file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    void encode(ImageWithDef image, File outputFile) throws IOException;

}
