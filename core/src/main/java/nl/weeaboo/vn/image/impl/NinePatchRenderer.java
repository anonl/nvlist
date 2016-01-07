package nl.weeaboo.vn.image.impl;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatchRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.impl.AbstractRenderable;

public class NinePatchRenderer extends AbstractRenderable implements INinePatchRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final Map<EArea, ITexture> textures = Maps.newEnumMap(EArea.class);

    private Insets2D insets = Insets2D.EMPTY;

    @Override
    public double getNativeWidth() {
        return insets.left + insets.right;
    }

    @Override
    public double getNativeHeight() {
        return insets.top + insets.bottom;
    }

    @Override
    public void render(IDrawable d, Area2D r, IDrawBuffer drawBuffer) {
        final int color = d.getColorARGB();
        final Area2D uv = ITexture.DEFAULT_UV;
        final Insets2D i = insets;

        // Center
        ITexture center = getTexture(EArea.CENTER);
        if (center != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y + i.bottom,
                    r.w - i.left - i.right, r.h - i.top - i.bottom);
            drawBuffer.drawQuad(d, color, center, bounds, uv);
        }

        // Corners
        ITexture topLeft = getTexture(EArea.TOP_LEFT);
        if (topLeft != null) {
            Area2D bounds = Area2D.of(r.x, r.y + r.h - i.top, i.left, i.top);
            drawBuffer.drawQuad(d, color, topLeft, bounds, uv);
        }
        ITexture topRight = getTexture(EArea.TOP_RIGHT);
        if (topRight != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y + r.h - i.top, i.right, i.top);
            drawBuffer.drawQuad(d, color, topRight, bounds, uv);
        }
        ITexture bottomLeft = getTexture(EArea.BOTTOM_LEFT);
        if (bottomLeft != null) {
            Area2D bounds = Area2D.of(r.x, r.y, i.left, i.bottom);
            drawBuffer.drawQuad(d, color, bottomLeft, bounds, uv);
        }
        ITexture bottomRight = getTexture(EArea.BOTTOM_RIGHT);
        if (bottomRight != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y, i.right, i.bottom);
            drawBuffer.drawQuad(d, color, bottomRight, bounds, uv);
        }

        // Sides
        ITexture top = getTexture(EArea.TOP);
        if (top != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y + r.h - i.top, r.w - i.left - i.right, i.top);
            drawBuffer.drawQuad(d, color, top, bounds, uv);
        }
        ITexture bottom = getTexture(EArea.BOTTOM);
        if (bottom != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y, r.w - i.left - i.right, i.bottom);
            drawBuffer.drawQuad(d, color, bottom, bounds, uv);
        }
        ITexture left = getTexture(EArea.LEFT);
        if (left != null) {
            Area2D bounds = Area2D.of(r.x, r.y + i.bottom, i.left, r.h - i.top - i.bottom);
            drawBuffer.drawQuad(d, color, left, bounds, uv);
        }
        ITexture right = getTexture(EArea.RIGHT);
        if (right != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y + i.bottom, i.right, r.h - i.top - i.bottom);
            drawBuffer.drawQuad(d, color, right, bounds, uv);
        }

    }

    @Override
    public ITexture getTexture(EArea area) {
        return textures.get(area);
    }

    @Override
    public void setTexture(EArea area, ITexture texture) {
        Preconditions.checkNotNull(area);
        textures.put(area, texture);
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
