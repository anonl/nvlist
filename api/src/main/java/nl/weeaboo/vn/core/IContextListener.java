package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.script.IScriptExceptionHandler;

/**
 * Event listener for {@link IContext}.
 */
public interface IContextListener extends Serializable, IScriptExceptionHandler {

    /** Called when a context becomes active. */
    void onContextActivated(IContext context);

    /** Called when a context becomes no longer active. */
    void onContextDeactivated(IContext context);

    /** Called when a context is destroyed. */
    void onContextDestroyed(IContext context);

}
