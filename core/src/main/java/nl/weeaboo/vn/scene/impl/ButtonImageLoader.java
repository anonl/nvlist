package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;

public final class ButtonImageLoader {

    private final IImageModule imageModule;

    private ButtonImageLoader(IImageModule imageModule) {
        this.imageModule = imageModule;
    }

    public void loadImages(IButton button, ResourceLoadInfo basePath) {
        loadImage(button, ButtonViewState.DEFAULT, basePath);
        loadImage(button, ButtonViewState.DISABLED, basePath.withFileSuffix("-disabled"));
        loadImage(button, ButtonViewState.PRESSED, basePath.withFileSuffix("-pressed"));
        loadImage(button, ButtonViewState.ROLLOVER, basePath.withFileSuffix("-rollover"));
    }

    private void loadImage(IButton button, ButtonViewState viewState, ResourceLoadInfo path) {
        button.setTexture(viewState, imageModule.getNinePatch(path, true));
    }

}
