package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.vn.buildtools.optimizer.image.IImageEncoder;

public interface IJpegEncoder extends IImageEncoder {

    byte[] encode(Pixmap pixmap, JpegEncoderParams params) throws IOException;

}
