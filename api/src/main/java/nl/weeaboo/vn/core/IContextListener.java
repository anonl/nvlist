package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IContextListener extends Serializable {

    /** Called when a context becomes active. */
    void onContextActivated(IContext context);

    /** Called when a context becomes no longer active. */
    void onContextDeactivated(IContext context);

    /** Called when a context is destroyed. */
    void onContextDestroyed(IContext context);

}
