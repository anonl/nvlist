package nl.weeaboo.vn.core;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.render.IRenderEnv;

public class RenderEnvStub implements IRenderEnv {

    private static final long serialVersionUID = 1L;

    private final Dim size = Dim.of(800, 600);

    @Override
    public Rect getGLClip() {
        return Rect.of(0, 0, getWidth(), getHeight());
    }

    @Override
    public int getWidth() {
        return size.w;
    }

    @Override
    public int getHeight() {
        return size.h;
    }

    @Override
    public Dim getVirtualSize() {
        return size;
    }

    @Override
    public Rect getRealClip() {
        return Rect.of(0, 0, getWidth(), getHeight());
    }

    @Override
    public Dim getScreenSize() {
        return size;
    }

    @Override
    public double getScale() {
        return 1.0;
    }

    @Override
    public Rect2D getGLScreenVirtualBounds() {
        return Rect2D.of(0, 0, getWidth(), getHeight());
    }

    @Override
    public boolean isTouchScreen() {
        return false;
    }

}
