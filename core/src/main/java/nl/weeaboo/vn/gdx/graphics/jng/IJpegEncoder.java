package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

public interface IJpegEncoder {

    byte[] encodeJpeg(Pixmap pixmap) throws IOException;

}
