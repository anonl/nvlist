package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import com.google.errorprone.annotations.CheckReturnValue;

import nl.weeaboo.vn.buildtools.optimizer.image.decoder.IImageDecoder;
import nl.weeaboo.vn.buildtools.optimizer.image.encoder.IImageEncoder;

/**
 * Represents an intermediate operation in the image processing pipeline.
 *
 * @see IImageDecoder
 * @see IImageEncoder
 */
interface IImageOperation {

    /**
     * Performs an operation on the image. A new pixmap may be allocated by this method. In that case, the
     * original pixmap is disposed.
     */
    @CheckReturnValue
    ImageWithDef process(ImageWithDef original) throws IOException;

}
