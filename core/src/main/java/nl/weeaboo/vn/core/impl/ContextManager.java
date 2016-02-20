package nl.weeaboo.vn.core.impl;

import java.util.Collection;

import com.google.common.base.Predicate;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.render.IDrawBuffer;

public class ContextManager implements IContextManager {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final IContextFactory<Context> contextFactory;

    private final DestructibleElemList<Context> contexts = new DestructibleElemList<Context>();

    public ContextManager(IContextFactory<Context> contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public final Context createContext() {
        Context context = contextFactory.newContext();
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
    public boolean isContextActive(IContext ctxt) {
        Context context = checkContains(ctxt);
        return context.isActive();
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
