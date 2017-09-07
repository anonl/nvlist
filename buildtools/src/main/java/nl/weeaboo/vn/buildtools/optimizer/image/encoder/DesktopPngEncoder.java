package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.io.Files;

import nl.weeaboo.vn.buildtools.optimizer.image.BufferedImageHelper;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

final class DesktopPngEncoder implements IPngEncoder {

    @Override
    public byte[] encode(Pixmap pixmap, PngEncoderParams params) throws IOException {
        BufferedImage image = BufferedImageHelper.toBufferedImage(pixmap,
                BufferedImageHelper.toBufferedImageType(pixmap.getFormat()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        image.flush();
        return out.toByteArray();
    }

    @Override
    public void encode(ImageWithDef image, File outputFile) throws IOException {
        byte[] bytes = encode(image.getPixmap(), new PngEncoderParams());
        Files.write(bytes, outputFile);
    }

}