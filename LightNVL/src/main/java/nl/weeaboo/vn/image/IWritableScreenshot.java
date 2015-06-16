package nl.weeaboo.vn.image;

import nl.weeaboo.common.Dim;

public interface IWritableScreenshot extends IScreenshot {

    void setPixels(ITextureData texData, Dim screenSize);

}
