package nl.weeaboo.vn.image.impl;

import nl.weeaboo.common.Dim;

public class PixmapDecodingScreenshot extends DecodingScreenshot {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    public PixmapDecodingScreenshot(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void tryLoad(byte[] data) {
        PixelTextureData pixels = PixelTextureData.fromImageFile(data, 0, data.length);
        setPixels(pixels, Dim.of(pixels.getWidth(), pixels.getHeight()));
    }

}
