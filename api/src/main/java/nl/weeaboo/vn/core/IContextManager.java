package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.vn.script.IScriptFunction;

/** Manages the active {@link IContext} and context lifetimes. */
public interface IContextManager extends Serializable, IUpdateable {

    IContext createContext();

    /** @see #createContext() */
    IContext createContext(IScriptFunction func);

    Collection<? extends IContext> getContexts();

    /**
     * @return A copy of the collection of currently active contexts.
     */
    Collection<? extends IContext> getActiveContexts();

    boolean isContextActive(IContext context);

    void setContextActive(IContext context, boolean active);

    void setRenderEnv(IRenderEnv env);

}
