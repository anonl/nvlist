package nl.weeaboo.vn.impl.image;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.common.Dim;

public class PixmapDecodingScreenshot extends DecodingScreenshot {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    public PixmapDecodingScreenshot(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void tryLoad(byte[] data) {
        Pixmap pixmap = new Pixmap(data, 0, data.length);
        // The stored data is already premultiplied
        PixelTextureData texData = PixelTextureData.fromPremultipliedPixmap(pixmap);

        setPixels(texData, Dim.of(texData.getWidth(), texData.getHeight()));
    }

}
