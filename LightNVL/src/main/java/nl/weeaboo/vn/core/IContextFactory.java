package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IContextFactory<C extends IContext> extends Serializable {

    public C newContext();

}
