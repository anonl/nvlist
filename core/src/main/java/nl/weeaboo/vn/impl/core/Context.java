package nl.weeaboo.vn.impl.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextListener;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThread;
import nl.weeaboo.vn.impl.signal.SignalUtil;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.PrefsChangeSignal;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;

/**
 * Default implementation of {@link IContext}.
 */
public class Context implements IContext {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private final Screen screen;
    private final LuaScriptContext scriptContext;
    private final ISkipState skipState;

    private final List<IContextListener> contextListeners = new CopyOnWriteArrayList<>();

    private boolean active;
    private boolean destroyed;

    public Context(ContextArgs contextArgs) {
        this.screen = Checks.checkNotNull(contextArgs.screen);
        this.scriptContext = Checks.checkNotNull(contextArgs.scriptContext);
        this.skipState = Checks.checkNotNull(contextArgs.skipState);
    }

    @Override
    public final void destroy() {
        if (!destroyed) {
            destroyed = true;

            scriptContext.destroy();
            LOG.debug("Context destroyed: {}", this);
            fireDestroyed();
        }
    }

    @Override
    public void addContextListener(IContextListener contextListener) {
        contextListeners.add(contextListener);
    }

    @Override
    public void removeContextListener(IContextListener contextListener) {
        contextListeners.remove(contextListener);
    }

    private void fireDestroyed() {
        for (IContextListener cl : contextListeners) {
            cl.onContextDestroyed(this);
        }
    }

    private void fireActiveStateChanged(final boolean activated) {
        for (IContextListener cl : contextListeners) {
            if (activated) {
                cl.onContextActivated(this);
            } else {
                cl.onContextDeactivated(this);
            }
        }
    }

    @Override
    public void onCurrent() {
        LuaTable globals = scriptContext.getGlobals();
        globals.rawset("context", scriptContext.getContextGlobals());
    }

    @Override
    public void updateScreen() {
        screen.update();

        // Handle skip mode
        IInput input = StaticEnvironment.INPUT.get();
        getSkipState().handleInput(input);
    }

    /** Draws the context's screen to the given draw buffer. */
    public void drawScreen(IDrawBuffer drawBuffer) {
        screen.draw(drawBuffer);
    }

    /** Performs tasks that must run in the OpenGL thread. This method is called by the framework. */
    public void updateInRenderThread() {
        screen.getOffscreenRenderTaskBuffer().update();
    }

    @Override
    public void updateScripts() {
        LuaScriptThread mainThread = scriptContext.getMainThread();
        boolean mainThreadWasRunnable = mainThread.isRunnable();

        IScriptExceptionHandler defaultExceptionHandler = scriptContext.getDefaultExceptionHandler();
        scriptContext.updateThreads(this, new IScriptExceptionHandler() {
            @Override
            public void onScriptException(IScriptThread thread, Exception exception) {
                defaultExceptionHandler.onScriptException(thread, exception);

                for (IScriptExceptionHandler ls : contextListeners) {
                    ls.onScriptException(thread, exception);
                }
            }
        });

        if (mainThreadWasRunnable && !mainThread.isRunnable()) {
            for (IContextListener cl : contextListeners) {
                cl.onMainThreadFinished(this);
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public IScreen getScreen() {
        return screen;
    }

    @Override
    public ISkipState getSkipState() {
        return skipState;
    }

    @Override
    public LuaScriptContext getScriptContext() {
        return scriptContext;
    }

    void setActive(boolean a) {
        if (active != a) {
            active = a;

            fireActiveStateChanged(a);
        }
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal.isUnhandled(RenderEnvChangeSignal.class)) {
            setRenderEnv(((RenderEnvChangeSignal)signal).getRenderEnv());
        }
        if (signal.isUnhandled(PrefsChangeSignal.class)) {
            onPrefsChanged(((PrefsChangeSignal)signal).getPrefsStore());
        }

        SignalUtil.forward(signal, screen);
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
