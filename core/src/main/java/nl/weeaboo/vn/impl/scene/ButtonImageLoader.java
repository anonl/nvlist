package nl.weeaboo.vn.impl.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;

public final class ButtonImageLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonImageLoader.class);

    private final IImageModule imageModule;

    public ButtonImageLoader(IImageModule imageModule) {
        this.imageModule = imageModule;
    }

    /**
     * Attempts to load the various images used by a button.
     */
    public void loadImages(IButton button, ResourceLoadInfo basePath) {
        if (!loadImage(button, ButtonViewState.DEFAULT, basePath, "")) {
            // Fallback if the previous image doesn't exist
            loadImage(button, ButtonViewState.DEFAULT, basePath, "normal");
        }

        // TODO: Support either different images, or putting all sub-rects in the same image with a subid prefix
        loadImage(button, ButtonViewState.DISABLED, basePath, "disabled");
        loadImage(button, ButtonViewState.PRESSED, basePath, "pressed");
        loadImage(button, ButtonViewState.ROLLOVER, basePath, "rollover");
    }

    private boolean loadImage(IButton button, ButtonViewState viewState, ResourceLoadInfo basePath, String suffix) {
        // Attempt to load as a single image file with sub-rects for each view state
        if (tryLoadImage(button, viewState, basePath.withAppendedSubId(suffix))) {
            return true;
        }

        if (!Strings.isNullOrEmpty(suffix)) {
            // Attempt to load as as separate image files for each view state
            ResourceLoadInfo path = basePath.withFileSuffix("-" + suffix);
            if (tryLoadImage(button, viewState, path)) {
                LOG.trace("Loading button image: {}, {}", viewState, path);
                return true;
            }
        }

        return false;
    }

    private boolean tryLoadImage(IButton button, ButtonViewState viewState, ResourceLoadInfo path) {
        INinePatch ninePatch = imageModule.getNinePatch(path, true);
        if (ninePatch != null) {
            LOG.trace("Loading button ninePatch: {}, {}", viewState, path);
            button.setTexture(viewState, ninePatch);
            return true;
        }

        ITexture tex = imageModule.getTexture(path, true);
        if (tex != null) {
            LOG.trace("Loading button texture: {}, {}", viewState, path);
            button.setTexture(viewState, tex);
            return true;
        }

        return false;
    }

}
