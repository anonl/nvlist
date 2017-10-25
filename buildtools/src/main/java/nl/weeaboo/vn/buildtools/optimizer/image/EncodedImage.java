package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public final class EncodedImage implements IEncodedResource {

    private final IEncodedResource encodedImage;
    private final ImageDefinition imageDefinition;

    public EncodedImage(IEncodedResource encodedImage, ImageDefinition imageDefinition) {
        this.encodedImage = encodedImage;
        this.imageDefinition = imageDefinition;
    }

    @Override
    public void dispose() {
        encodedImage.dispose();
    }

    @Override
    public byte[] readBytes() throws IOException {
        return encodedImage.readBytes();
    }

    /** Returns the {@link IImageDefinition} accompanying the image. */
    public ImageDefinition getDef() {
        return imageDefinition;
    }

}
