package nl.weeaboo.vn.impl.image;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.impl.scene.Button;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.scene.TextDrawable;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;

public class ImageModuleStub implements IImageModule {

    private static final long serialVersionUID = 1L;

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath filename) {
        return null;
    }

    @Override
    public IImageDrawable createImage(ILayer layer) {
        return new ImageDrawable();
    }

    @Override
    public ITextDrawable createTextDrawable(ILayer layer) {
        return new TextDrawable();
    }

    @Override
    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        return new Button(scriptContext.getEventDispatcher());
    }

    @Override
    public ITexture getTexture(FilePath filename) {
        return getTexture(new ResourceLoadInfo(filename), false);
    }

    @Override
    public ITexture getTexture(ResourceLoadInfo info, boolean suppressErrors) {
        return new TestTexture();
    }

    @Override
    public @Nullable INinePatch getNinePatch(ResourceLoadInfo path, boolean suppressErrors) {
        return null;
    }

    @Override
    public ITexture getColorTexture(int argb) {
        PixelTextureData texData = TestImageUtil.newTestTextureData(argb, 1, 1);
        return createTexture(texData, 1, 1);
    }

    @Override
    public ITexture createTexture(ITextureData texData, double scaleX, double scaleY) {
        return new TestTexture(texData);
    }

    @Override
    public ITexture createTexture(IScreenshot ss) {
        return new TestTexture(ss.getPixels());
    }

    @Override
    public Collection<FilePath> getImageFiles(FilePath folder) {
        return Collections.emptySet();
    }

    @Override
    public void setImageResolution(Dim size) {
    }

    @Override
    public IScreenshot screenshot(ILayer layer, short z, boolean isVolatile, boolean clipEnabled) {
        WritableScreenshot screenshot = new WritableScreenshot(z, isVolatile);
        screenshot.cancel();
        return screenshot;
    }

    @Override
    public void preload(FilePath filename) {
    }

}
