package nl.weeaboo.vn.core.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextListener;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.impl.lua.LuaScriptContext;

public class Context implements IContext {

	private static final long serialVersionUID = CoreImpl.serialVersionUID;
	private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private final Screen screen;
    private final LuaScriptContext scriptContext;
    private final ISkipState skipState = new SkipState();

	private final List<IContextListener> contextListeners = new CopyOnWriteArrayList<IContextListener>();

	private boolean active;
	private boolean destroyed;

    public Context(ContextArgs contextArgs) {
		this.screen = Checks.checkNotNull(contextArgs.screen);
		this.scriptContext = contextArgs.scriptContext;
	}

	@Override
	public final void destroy() {
		if (!destroyed) {
			destroyed = true;

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
	public void updateScreen() {
	    screen.update();
	}

    public void drawScreen(IDrawBuffer drawBuffer) {
        screen.draw(drawBuffer);
    }

	@Override
	public void updateScripts() {
	    scriptContext.updateThreads(this);
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
    public void setRenderEnv(IRenderEnv env) {
        screen.setRenderEnv(env);
    }

}
