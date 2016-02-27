package nl.weeaboo.vn.image.impl;

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
    private final Map<EArea, ITexture> textures = Maps.newEnumMap(EArea.class);
    private Insets2D insets = Insets2D.EMPTY;
    // ------

    public NinePatch() {
    }

    public NinePatch(ITexture tex) {
        this();

        Preconditions.checkNotNull(tex);
        textures.put(EArea.CENTER, tex);
    }

    @Override
    public void set(INinePatch other) {
        textures.clear();
        for (EArea area : EArea.values()) {
            ITexture tex = other.getTexture(area);
            if (tex != null) {
                textures.put(area, tex);
            }
        }

        insets = other.getInsets();
    }

    @Override
    public double getNativeWidth() {
        return insets.left + insets.right;
    }

    @Override
    public double getNativeHeight() {
        return insets.top + insets.bottom;
    }

    @Override
    public ITexture getTexture(EArea area) {
        return textures.get(area);
    }

    @Override
    public void setTexture(EArea area, ITexture texture) {
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

}
