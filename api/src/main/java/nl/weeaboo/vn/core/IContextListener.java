package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IContextListener extends Serializable {

    public void onContextActivated(IContext context);

    public void onContextDeactivated(IContext context);

    public void onContextDestroyed(IContext context);

}
