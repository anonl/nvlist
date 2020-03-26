package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

/**
 * JPEG image file encoder.
 */
public interface IJpegEncoder extends IImageEncoder {

    /**
     * Encodes an image to JPEG.
     *
     * @throws IOException If an I/O error occurs while encoding the file.
     */
    byte[] encode(Pixmap pixmap, JpegEncoderParams params) throws IOException;

}
