package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.Collection;

/** Manages the active {@link IContext} and context lifetimes. */
public interface IContextManager extends Serializable, IUpdateable {

    IContext createContext();

    Collection<? extends IContext> getContexts();

    /**
     * @return A copy of the collection of currently active contexts.
     */
    Collection<? extends IContext> getActiveContexts();

    boolean isContextActive(IContext context);

    void setContextActive(IContext context, boolean active);

    void setRenderEnv(IRenderEnv env);

}
