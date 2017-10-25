package nl.weeaboo.vn.buildtools.optimizer.image.decoder;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.gdx.graphics.PixmapLoader;

final class DefaultImageDecoder implements IImageDecoder {

    @Override
    public ImageWithDef decode(EncodedImage encodedImage) throws IOException {
        byte[] imageBytes = encodedImage.readBytes();

        Pixmap pixmap = PixmapLoader.load(imageBytes, 0, imageBytes.length);

        return new ImageWithDef(pixmap, encodedImage.getDef());
    }

}
