package nl.weeaboo.vn.core;

import nl.weeaboo.vn.script.IScriptThread;

/**
 * Empty implementation of {@link IContextListener}.
 */
public class ContextListener implements IContextListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void onScriptException(IScriptThread thread, Exception exception) {
    }

    @Override
    public void onContextActivated(IContext context) {
    }

    @Override
    public void onContextDeactivated(IContext context) {
    }

    @Override
    public void onContextDestroyed(IContext context) {
    }

}
