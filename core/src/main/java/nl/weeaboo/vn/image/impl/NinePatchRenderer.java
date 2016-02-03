package nl.weeaboo.vn.image.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatchRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.impl.AbstractRenderable;

public class NinePatchRenderer extends AbstractRenderable implements INinePatchRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final INinePatch ninePatch = new NinePatch();

    @Override
    public void render(IDrawBuffer drawBuffer, IDrawable d, Area2D r) {
        final int color = d.getColorARGB();
        final Area2D uv = ITexture.DEFAULT_UV;
        final Insets2D i = ninePatch.getInsets();

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
    public double getNativeWidth() {
        return ninePatch.getNativeWidth();
    }

    @Override
    public double getNativeHeight() {
        return ninePatch.getNativeHeight();
    }

    @Override
    public ITexture getTexture(EArea area) {
        return ninePatch.getTexture(area);
    }

    @Override
    public void setTexture(EArea area, ITexture texture) {
        ninePatch.setTexture(area, texture);
    }

    @Override
    public Insets2D getInsets() {
        return ninePatch.getInsets();
    }

    @Override
    public void setInsets(Insets2D i) {
        ninePatch.setInsets(i);
    }

    @Override
    public void set(INinePatch other) {
        ninePatch.set(other);
    }

}
