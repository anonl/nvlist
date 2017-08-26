package nl.weeaboo.vn.impl.core;

import java.util.Collection;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.ScriptException;

public final class ContextManager implements IContextManager {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ContextManager.class);

    private final IContextFactory<Context> contextFactory;

    private final DestructibleElemList<Context> contexts = new DestructibleElemList<>();

    public ContextManager(IContextFactory<Context> contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public final Context createContext() {
        return createContext(null);
    }

    @Override
    public final Context createContext(IScriptFunction func) {
        Context context = contextFactory.newContext();
        if (func != null) {
            try {
                LuaScriptUtil.callFunction(context, func);
            } catch (ScriptException e) {
                LOG.warn("Exception while initializing new context", e);
            }
        }

        register(context);
        return context;
    }

    private void register(Context context) {
        if (contexts.contains(context)) {
            return;
        }

        contexts.add(context);
    }

    private Context checkContains(IContext ctxt) {
        for (Context context : contexts) {
            if (context == ctxt) {
                return context;
            }
        }
        throw new IllegalArgumentException("Context (" + ctxt + ") is not contained by this contextmanager.");
    }

    @Override
    public void update() {
        Collection<Context> active = getActiveContexts();
        for (IContext context : active) {
            context.updateScreen();
        }
        for (IContext context : active) {
            context.updateScripts();
        }
    }

    /** Draws all contexts to the given draw buffer. */
    public void draw(IDrawBuffer drawBuffer) {
        for (Context context : getActiveContexts()) {
            context.drawScreen(drawBuffer);
        }
    }

    @Override
    public Collection<Context> getContexts() {
        return contexts.getSnapshot();
    }

    @Override
    public Collection<Context> getActiveContexts() {
        return contexts.getSnapshot(new Predicate<Context>() {
            @Override
            public boolean apply(Context context) {
                return context.isActive();
            }
        });
    }

    @Override
    public @Nullable IContext getPrimaryContext() {
        return Iterables.get(getActiveContexts(), 0, null);
    }

    @Override
    public void setContextActive(IContext ctxt, boolean active) {
        Context context = checkContains(ctxt);
        context.setActive(active);
    }

    @Override
    public void setRenderEnv(IRenderEnv env) {
        contextFactory.setRenderEnv(env);

        for (IContext context : contexts) {
            context.setRenderEnv(env);
        }
    }

}
