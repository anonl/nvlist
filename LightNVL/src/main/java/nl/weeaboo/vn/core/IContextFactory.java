package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IContextFactory<C extends IContext> extends Serializable {

    C newContext();

    void setRenderEnv(IRenderEnv env);

}
