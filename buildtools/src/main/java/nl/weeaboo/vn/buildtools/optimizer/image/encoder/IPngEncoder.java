package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

public interface IPngEncoder extends IImageEncoder {

    /**
     * Encodes an image to PNG.
     *
     * @throws IOException If an I/O error occurs while encoding the file.
     */
    byte[] encode(Pixmap pixmap, PngEncoderParams params) throws IOException;

}
