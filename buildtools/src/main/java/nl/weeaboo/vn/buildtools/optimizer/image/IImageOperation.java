package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.image.decoder.IImageDecoder;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.IImageEncoder;

/**
 * Represents an intermediate operation in the image processing pipeline.
 *
 * @see IImageDecoder
 * @see IImageEncoder
 */
interface IImageOperation {

    ImageWithDef process(ImageWithDef original) throws IOException;

}
