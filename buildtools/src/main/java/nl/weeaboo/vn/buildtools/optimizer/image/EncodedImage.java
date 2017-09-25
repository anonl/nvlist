package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public final class EncodedImage implements Disposable {

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

    public byte[] readImageBytes() throws IOException {
        return encodedImage.readBytes();
    }

    public ImageDefinition getDef() {
        return imageDefinition;
    }

}
