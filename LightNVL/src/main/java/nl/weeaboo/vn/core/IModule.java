package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IModule extends Serializable, IUpdateable {

    void destroy();

}
