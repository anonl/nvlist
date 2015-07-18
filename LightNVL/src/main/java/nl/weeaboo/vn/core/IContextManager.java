package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.Collection;

/** Manages the active {@link IContext} and context lifetimes. */
public interface IContextManager extends Serializable, IUpdateable {

    public IContext createContext();

    public Collection<? extends IContext> getContexts();

    /**
     * @return A copy of the collection of currently active contexts.
     */
    public Collection<? extends IContext> getActiveContexts();

    public boolean isContextActive(IContext context);

    public void setContextActive(IContext context, boolean active);

    public void setRenderEnv(IRenderEnv env);

}
