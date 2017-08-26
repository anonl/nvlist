package nl.weeaboo.vn.impl.scene;

import java.io.Serializable;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.signal.SignalSupport;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.VisualElementDestroySignal;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;
import nl.weeaboo.vn.signal.TickSignal;

public abstract class VisualElement implements IVisualElement {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    /** May be null */
    IVisualGroup parent;

    private final SignalSupport signalSupport = new SignalSupport();

    private short z;
    private boolean visible = true;
    private boolean destroyed;

    private ILayoutElem layoutAdapter;

    public VisualElement() {
        // Add self at index 0
        signalSupport.addSignalHandler(0, new SelfSignalHandler(this));
    }

    @Override
    public final void destroy() {
        if (!destroyed) {
            sendSignal(new VisualElementDestroySignal(this));

            destroyed = true;

            onDestroyed();
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
    public final void handleSignal(ISignal signal) {
        signalSupport.handleSignal(signal);
    }

    protected void onTick() {
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
        // Default implementation for non-transformed elements/groups
        for (IVisualElement elem : SceneUtil.getChildren(this, VisualOrdering.FRONT_TO_BACK)) {
            elem.handleInput(parentTransform, input);
        }
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

    /**
     * @see IVisualElement#getZ()
     */
    public void setZ(short z) {
        this.z = z;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility flag.
     */
    public void setVisible(boolean v) {
        visible = v;
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.EMPTY;
    }

    @Override
    public @Nullable IRenderEnv getRenderEnv() {
        return (parent != null ? parent.getRenderEnv() : null);
    }

    @Override
    public final ILayoutElem getLayoutAdapter() {
        if (layoutAdapter == null) {
            layoutAdapter = createLayoutAdapter();
        }
        return layoutAdapter;
    }

    protected abstract ILayoutElem createLayoutAdapter();

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
                self.onRenderEnvChanged(((RenderEnvChangeSignal)signal).getRenderEnv());
            }
        }

    }

}
