package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.VisualElementDestroySignal;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;
import nl.weeaboo.vn.signal.TickSignal;
import nl.weeaboo.vn.signal.impl.SignalSupport;

public class VisualElement implements IVisualElement {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    /** May be null */
    IVisualGroup parent;

    private final SignalSupport signalSupport = new SignalSupport();

    private short z;
    private boolean visible = true;
    private boolean destroyed;

    public VisualElement() {
        // Add self at index 0
        signalSupport.addSignalHandler(0, new SelfSignalHandler(this));
    }

    @Override
    public final void destroy() {
        if (!destroyed) {
            destroyed = true;

            onDestroyed();

            sendSignal(new VisualElementDestroySignal(this));
        }
    }

    protected void onDestroyed() {
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public <T extends ISignalHandler & Serializable> void addSignalHandler(int order, T handler) {
        signalSupport.addSignalHandler(order, handler);
    }

    @Override
    public void removeSignalHandler(ISignalHandler handler) {
        signalSupport.removeSignalHandler(handler);
    }

    void sendSignal(ISignal signal) {
        SceneUtil.sendSignal(this, signal);
    }

    @Override
    public void handleSignal(ISignal signal) {
        signalSupport.handleSignal(signal);
    }

    protected void onTick() {
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
    }

    /**
     * @param env The new rendering environment.
     */
    protected void onRenderEnvChanged(IRenderEnv env) {
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
    }

    @Override
    public IVisualGroup getParent() {
        return parent;
    }

    @Override
    public void setParent(IVisualGroup parent) {
        this.parent = parent;
    }

    @Override
    public final short getZ() {
        return z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.EMPTY;
    }

    @Override
    public IRenderEnv getRenderEnv() {
        return (parent != null ? parent.getRenderEnv() : null);
    }

    private static class SelfSignalHandler implements ISignalHandler, Serializable {

        private static final long serialVersionUID = VisualElement.serialVersionUID;

        private VisualElement self;

        public SelfSignalHandler(VisualElement self) {
            this.self = Checks.checkNotNull(self);
        }

        @Override
        public void handleSignal(ISignal signal) {
            if (!signal.isHandled() && signal instanceof TickSignal) {
                self.onTick();
            }
            if (!signal.isHandled() && signal instanceof RenderEnvChangeSignal) {
                self.onRenderEnvChanged(((RenderEnvChangeSignal)signal).renderEnv);
            }
        }

    }
}
