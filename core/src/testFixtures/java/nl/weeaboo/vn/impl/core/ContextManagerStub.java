package nl.weeaboo.vn.impl.core;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.impl.signal.SignalUtil;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.signal.ISignal;

public final class ContextManagerStub implements IContextManager {

    private static final long serialVersionUID = 1L;

    @Override
    public void update() {
    }

    @Override
    public IContext createContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContext createContext(IScriptFunction func) {
        return createContext();
    }

    @Override
    public IContext callInContext(IScriptFunction func) {
        return createContext();
    }

    @Override
    public Collection<? extends IContext> getContexts() {
        return ImmutableList.of();
    }

    @Override
    public Collection<? extends IContext> getActiveContexts() {
        return ImmutableList.of();
    }

    @Override
    public @Nullable IContext getPrimaryContext() {
        return null;
    }

    @Override
    public void setContextActive(IContext context, boolean active) {
    }

    @Override
    public void handleSignal(ISignal signal) {
        SignalUtil.forward(signal, getContexts());
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
