package nl.weeaboo.vn.buildtools.optimizer.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Pixmap;

final class DesktopPngEncoder {

    public byte[] encodePng(Pixmap pixmap) throws IOException {
        BufferedImage image = BufferedImageHelper.toBufferedImage(pixmap,
                BufferedImageHelper.toBufferedImageType(pixmap.getFormat()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        image.flush();
        return out.toByteArray();
    }

}