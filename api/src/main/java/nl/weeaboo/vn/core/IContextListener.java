package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IContextListener extends Serializable {

    void onContextActivated(IContext context);

    void onContextDeactivated(IContext context);

    void onContextDestroyed(IContext context);

}
