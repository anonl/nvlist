package nl.weeaboo.vn.render.impl;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.image.impl.AbstractScreenshot;

public class WritableScreenshot extends AbstractScreenshot implements IWritableScreenshot {

	private static final long serialVersionUID = RenderImpl.serialVersionUID;

	public WritableScreenshot(short z, boolean isVolatile) {
		super(z, isVolatile);
	}
	
	@Override
    public void setPixels(ITextureData texData, Dim screenSize) {
        super.setPixels(texData, screenSize);
    }	
    
}
