package nl.weeaboo.vn.scene;

import java.io.Serializable;

import javax.annotation.Nullable;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;

public class VisualElementStub implements IVisualElement {

    private static final long serialVersionUID = 1L;

    @Override
    public void destroy() {
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void handleSignal(ISignal signal) {
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
    }

    @Override
    public <T extends ISignalHandler & Serializable> void addSignalHandler(int order, T handler) {
    }

    @Override
    public void removeSignalHandler(ISignalHandler handler) {
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
    }

    @Override
    public @Nullable IVisualGroup getParent() {
        return null;
    }

    @Override
    public void setParent(IVisualGroup visualGroup) {
    }

    @Override
    public short getZ() {
        return 0;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.EMPTY;
    }

    @Override
    public IRenderEnv getRenderEnv() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILayoutElem getLayoutAdapter() {
        throw new UnsupportedOperationException();
    }

}
