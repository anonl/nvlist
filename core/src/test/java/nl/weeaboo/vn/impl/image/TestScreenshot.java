package nl.weeaboo.vn.impl.image;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.image.IWritableScreenshot;

public class TestScreenshot extends AbstractScreenshot implements IWritableScreenshot {

    private static final long serialVersionUID = 1L;

    public TestScreenshot() {
        super((short)0, false);
    }

    @Override
    public void setPixels(ITextureData texData, Dim screenSize) {
        super.setPixels(texData, screenSize);
    }

    /**
     * Calls {@link #setPixels(ITextureData, Dim)} with a dummy texture.
     */
    public void setPixels(int w, int h) {
        PixelTextureData texData = TestImageUtil.newTestTextureData(w, h);
        setPixels(texData, Dim.of(1280, 720));
    }

}
