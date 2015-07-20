package nl.weeaboo.vn.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.render.IDrawBuffer;

public class ContextManager implements IContextManager {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final IContextFactory<Context> contextFactory;

    private final List<Context> contexts = new ArrayList<Context>();

    public ContextManager(IContextFactory<Context> contextFactory) {
        this.contextFactory = contextFactory;
    }

    //Functions
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
        for (IContext context : getActiveContexts()) {
            context.updateScreen();
        }
    }

    public void draw(IDrawBuffer drawBuffer) {
        for (Context context : getActiveContexts()) {
            context.drawScreen(drawBuffer);
        }
    }

    //Getters
    @Override
    public Collection<Context> getContexts() {
        return Collections.unmodifiableCollection(contexts);
    }

    @Override
    public Collection<Context> getActiveContexts() {
        List<Context> active = new ArrayList<Context>(2);
        for (Context context : contexts) {
            if (context.isActive()) {
                active.add(context);
            }
        }
        return Collections.unmodifiableCollection(active);
    }

    @Override
    public boolean isContextActive(IContext ctxt) {
        Context context = checkContains(ctxt);
        return context.isActive();
    }

    //Setters
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

    @Override
    public Entity findEntity(int entityId) {
        for (IContext context : contexts) {
            Entity e = context.findEntity(entityId);
            if (e != null) {
                return e;
            }
        }
        return null;
    }

}
