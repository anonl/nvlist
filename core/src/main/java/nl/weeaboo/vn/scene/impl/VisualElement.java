package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.DestroySignal;
import nl.weeaboo.vn.scene.signal.ISignal;
import nl.weeaboo.vn.scene.signal.RenderEnvChangeSignal;

public class VisualElement implements IVisualElement {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    /** May be null */
    IVisualGroup parent;

    private short z;
    private boolean visible = true;
    private boolean destroyed;

    @Override
    public final void destroy() {
        if (!destroyed) {
            destroyed = true;

            onDestroyed();

            sendSignal(new DestroySignal(this));
        }
    }

    protected void onDestroyed() {
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    void sendSignal(ISignal signal) {
        SceneUtil.sendSignal(this, signal);
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal instanceof RenderEnvChangeSignal) {
            onRenderEnvChanged(((RenderEnvChangeSignal)signal).getRenderEnv());
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

}
