package nl.weeaboo.vn.impl.image;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatchRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.scene.AbstractRenderable;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;

public class NinePatchRenderer extends AbstractRenderable implements INinePatchRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final INinePatch ninePatch = new NinePatch();

    @Override
    public void render(IDrawBuffer drawBuffer, IDrawable d, Area2D r) {
        final int color = d.getColorARGB();
        final Area2D uv = ITexture.DEFAULT_UV;
        final Insets2D i = ninePatch.getInsets();

        // Center
        ITexture center = getTexture(AreaId.CENTER);
        if (center != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y + i.top,
                    r.w - i.left - i.right, r.h - i.top - i.bottom);
            drawBuffer.drawQuad(d, color, center, bounds, uv);
        }

        // Corners
        ITexture topLeft = getTexture(AreaId.TOP_LEFT);
        if (topLeft != null) {
            Area2D bounds = Area2D.of(r.x, r.y, i.left, i.top);
            drawBuffer.drawQuad(d, color, topLeft, bounds, uv);
        }
        ITexture topRight = getTexture(AreaId.TOP_RIGHT);
        if (topRight != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y, i.right, i.top);
            drawBuffer.drawQuad(d, color, topRight, bounds, uv);
        }
        ITexture bottomLeft = getTexture(AreaId.BOTTOM_LEFT);
        if (bottomLeft != null) {
            Area2D bounds = Area2D.of(r.x, r.y + r.h - i.bottom, i.left, i.bottom);
            drawBuffer.drawQuad(d, color, bottomLeft, bounds, uv);
        }
        ITexture bottomRight = getTexture(AreaId.BOTTOM_RIGHT);
        if (bottomRight != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y + r.h - i.bottom, i.right, i.bottom);
            drawBuffer.drawQuad(d, color, bottomRight, bounds, uv);
        }

        // Sides
        ITexture top = getTexture(AreaId.TOP);
        if (top != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y, r.w - i.left - i.right, i.top);
            drawBuffer.drawQuad(d, color, top, bounds, uv);
        }
        ITexture bottom = getTexture(AreaId.BOTTOM);
        if (bottom != null) {
            Area2D bounds = Area2D.of(r.x + i.left, r.y + r.h - i.bottom, r.w - i.left - i.right, i.bottom);
            drawBuffer.drawQuad(d, color, bottom, bounds, uv);
        }
        ITexture left = getTexture(AreaId.LEFT);
        if (left != null) {
            Area2D bounds = Area2D.of(r.x, r.y + i.top, i.left, r.h - i.top - i.bottom);
            drawBuffer.drawQuad(d, color, left, bounds, uv);
        }
        ITexture right = getTexture(AreaId.RIGHT);
        if (right != null) {
            Area2D bounds = Area2D.of(r.x + r.w - i.right, r.y + i.top, i.right, r.h - i.top - i.bottom);
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
    public ITexture getTexture(AreaId area) {
        return ninePatch.getTexture(area);
    }

    @Override
    public void setTexture(AreaId area, ITexture texture) {
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
