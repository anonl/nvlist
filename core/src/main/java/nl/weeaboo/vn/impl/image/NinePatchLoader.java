package nl.weeaboo.vn.impl.image;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.AreaId;
import nl.weeaboo.vn.image.ITexture;

public final class NinePatchLoader {

    private static final Logger LOG = LoggerFactory.getLogger(NinePatchLoader.class);

    private IImageModule imageModule;

    public NinePatchLoader(IImageModule imageModule) {
        this.imageModule = imageModule;
    }

    /**
     * Attempts to load a nine-patch resource.
     * @return The nine-patch, or {@code null} if loading failed.
     */
    public @Nullable INinePatch loadNinePatch(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        NinePatch ninePatch = new NinePatch();
        for (AreaId area : AreaId.values()) {
            String subId = getSubId(area);
            ResourceLoadInfo subPath = loadInfo.withAppendedSubId(subId);
            ITexture tex = imageModule.getTexture(subPath, suppressErrors);
            if (tex == null) {
                if (!suppressErrors) {
                    LOG.warn("Incomplete ninePatch: {}, {}", area, subPath);
                }
                return null;
            } else {
                ninePatch.setTexture(area, tex);
            }
        }
        ninePatch.setInsets(NinePatch.calculateNativeInsets(ninePatch));
        return ninePatch;
    }

    private String getSubId(AreaId area) {
        switch (area) {
        case TOP_LEFT: return "topleft";
        case TOP: return "top";
        case TOP_RIGHT: return "topright";
        case LEFT: return "left";
        case CENTER: return "center";
        case RIGHT: return "right";
        case BOTTOM_LEFT: return "bottomleft";
        case BOTTOM: return "bottom";
        case BOTTOM_RIGHT: return "bottomright";
        }

        throw new IllegalArgumentException("Unsupported area: " + area);
    }

}
