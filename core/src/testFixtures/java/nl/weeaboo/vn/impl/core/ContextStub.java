package nl.weeaboo.vn.impl.core;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextListener;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.signal.ISignal;

public final class ContextStub implements IContext {

    private static final long serialVersionUID = 1L;

    private boolean active = true;
    private boolean destroyed;

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void addContextListener(IContextListener contextListener) {
    }

    @Override
    public void removeContextListener(IContextListener contextListener) {
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean a) {
        active = a;
    }

    @Override
    public void updateScreen() {
    }

    @Override
    public void updateScripts() {
    }

    @Override
    public void onCurrent() {
    }

    @Override
    public IScreen getScreen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IScriptContext getScriptContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISkipState getSkipState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleSignal(ISignal signal) {
    }

    @Deprecated
    @Override
    public void setRenderEnv(IRenderEnv env) {
    }

    @Deprecated
    @Override
    public void onPrefsChanged(IPreferenceStore config) {
    }

}
