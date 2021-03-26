package nl.weeaboo.vn.impl.image;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.impl.core.AbstractModule;
import nl.weeaboo.vn.impl.scene.Button;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.scene.TextDrawable;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.text.FontStoreMock;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.text.ILoadingFontStore;

public class ImageModuleStub extends AbstractModule implements IImageModule {

    private static final long serialVersionUID = 1L;

    private final ILoadingFontStore fontStore = new FontStoreMock();

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
        return new TextDrawable(fontStore);
    }

    @Override
    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        return new Button(scriptContext.getEventDispatcher(), fontStore);
    }

    @Override
    public ITexture getTexture(FilePath filename) {
        return getTexture(LuaScriptUtil.createLoadInfo(MediaType.IMAGE, filename), false);
    }

    @Override
    public ITexture getTexture(ResourceLoadInfo info, boolean suppressErrors) {
        return new TextureMock();
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
        return new TextureMock(texData);
    }

    @Override
    public ITexture createTexture(IScreenshot ss) {
        return new TextureMock(ss.getPixels());
    }

    @Override
    public Collection<FilePath> getImageFiles(FilePath folder) {
        return Collections.emptySet();
    }

    @Override
    public void setImageResolution(Dim size) {
    }

    @Override
    public IScreenshot screenshot() {
        return EmptyScreenshot.getInstance();
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
