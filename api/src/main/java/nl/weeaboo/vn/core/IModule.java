package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IModule extends Serializable, IUpdateable {

    /**
     * Destroys the module, cleaning up any native resources.
     */
    void destroy();

}
