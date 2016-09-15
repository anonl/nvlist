package nl.weeaboo.vn.image.impl;

import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.EArea;
import nl.weeaboo.vn.image.ITexture;

public final class NinePatchLoader {

    private IImageModule imageModule;

    public NinePatchLoader(IImageModule imageModule) {
        this.imageModule = imageModule;
    }

    public INinePatch loadNinePatch(ResourceLoadInfo loadInfo, boolean suppressErrors) {
        NinePatch ninePatch = new NinePatch();
        for (EArea area : EArea.values()) {
            String subId = getSubId(area);
            ITexture tex = imageModule.getTexture(loadInfo.withSubId(subId), suppressErrors);
            if (tex != null) {
                ninePatch.setTexture(area, tex);
            }
        }
        ninePatch.setInsets(NinePatch.calculateNativeInsets(ninePatch));
        return ninePatch;
    }

    private String getSubId(EArea area) {
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
        default:
            throw new IllegalArgumentException("Unsupported area: " + area);
        }
    }

}
