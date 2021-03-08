package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;

import nl.weeaboo.vn.render.IRenderEnvConsumer;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.signal.ISignalHandler;

/**
 * Manages the active {@link IContext} and context lifetimes.
 */
public interface IContextManager extends Serializable, IUpdateable, IRenderEnvConsumer, IPrefsChangeListener,
        ISignalHandler {

    /**
     * Creates a new context and registers it with the context manager.
     * @return The newly creates context.
     */
    IContext createContext();

    /**
     * @see #createContext()
     */
    IContext createContext(IScriptFunction func);

    /**
     * Calls the given function in a temporary context. The current context is paused until the function
     * returns. When the function returns, the temporary context is destroyed.
     */
    IContext callInContext(IScriptFunction func);

    /**
     * Returns a read-only collection containing the registered contexts.
     */
    Collection<? extends IContext> getContexts();

    /**
     * @return A copy of the collection of currently active contexts.
     */
    Collection<? extends IContext> getActiveContexts();

    /**
     * Returns the 'most important' active context, or {@code null} if no context is active.
     */
    @Nullable IContext getPrimaryContext();

    /**
     * Activates/deactivates the given context.
     *
     * @throws IllegalArgumentException If the given context isn't registered with this context manager.
     */
    void setContextActive(IContext context, boolean active);

}
