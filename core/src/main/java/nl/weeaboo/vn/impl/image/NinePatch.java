package nl.weeaboo.vn.impl.image;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;

public final class NinePatch implements INinePatch {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    // --- Don't forget to update set() after adding fields ---
    private final Map<AreaId, ITexture> textures = Maps.newEnumMap(AreaId.class);
    private Insets2D insets = Insets2D.EMPTY;
    // ------

    public NinePatch() {
    }

    public NinePatch(ITexture tex) {
        this();

        Preconditions.checkNotNull(tex);
        textures.put(AreaId.CENTER, tex);
    }

    @Override
    public void set(INinePatch other) {
        textures.clear();
        for (AreaId area : AreaId.values()) {
            ITexture tex = other.getTexture(area);
            if (tex != null) {
                textures.put(area, tex);
            }
        }

        insets = other.getInsets();
    }

    @Override
    public double getNativeWidth() {
        double width = insets.left + insets.right;
        ITexture center = getTexture(AreaId.CENTER);
        if (center != null) {
            width += center.getWidth();
        }
        return width;
    }

    @Override
    public double getNativeHeight() {
        double height = insets.top + insets.bottom;
        ITexture center = getTexture(AreaId.CENTER);
        if (center != null) {
            height += center.getHeight();
        }
        return height;
    }

    @Override
    public ITexture getTexture(AreaId area) {
        return textures.get(area);
    }

    @Override
    public void setTexture(AreaId area, ITexture texture) {
        if (texture != null) {
            textures.put(area, texture);
        } else {
            textures.remove(area);
        }
    }

    @Override
    public Insets2D getInsets() {
        return insets;
    }

    @Override
    public void setInsets(Insets2D i) {
        this.insets = Checks.checkNotNull(i);
    }

    /**
     * Calculates the insets based on the native sizes of the textures in the nine-patch.
     */
    public static Insets2D calculateNativeInsets(INinePatch ninePatch) {
        double top = 0;
        double right = 0;
        double bottom = 0;
        double left = 0;
        for (AreaId area : AreaId.values()) {
            ITexture tex = ninePatch.getTexture(area);
            if (tex == null) {
                continue;
            }

            double tw = tex.getWidth();
            double th = tex.getHeight();
            if (area.isTop()) {
                top = Math.max(top, th);
            }
            if (area.isRight()) {
                right = Math.max(right, tw);
            }
            if (area.isBottom()) {
                bottom = Math.max(bottom, th);
            }
            if (area.isLeft()) {
                left = Math.max(left, tw);
            }
        }
        return Insets2D.of(top, right, bottom, left);
    }

}
