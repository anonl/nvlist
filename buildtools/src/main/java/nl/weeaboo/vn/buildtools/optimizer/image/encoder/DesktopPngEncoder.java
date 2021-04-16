package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.optimizer.image.BufferedImageUtil;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;

final class DesktopPngEncoder implements IPngEncoder {

    @Override
    public byte[] encode(Pixmap pixmap, PngEncoderParams params) throws IOException {
        BufferedImage image = BufferedImageUtil.toBufferedImage(pixmap);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        image.flush();
        return out.toByteArray();
    }

    @Override
    public EncodedImage encode(ImageWithDef image) throws IOException {
        byte[] bytes = encode(image.getPixmap(), new PngEncoderParams());

        IEncodedResource encodedImage = EncodedResource.fromBytes(bytes);
        ImageDefinitionBuilder imageDef = image.getDef().builder();
        imageDef.setFilename(Filenames.replaceExt(imageDef.getFilename(), "png"));
        return new EncodedImage(encodedImage, imageDef.build());
    }

}