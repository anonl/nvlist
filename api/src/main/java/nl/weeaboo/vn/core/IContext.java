package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.render.IRenderEnvConsumer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;

/**
 * High-level container of state. In a more general-purpose game engine this concept is usually called
 * 'scene', 'stage' or 'level'.
 *
 * @see IContextManager
 */
public interface IContext extends Serializable, IDestructible, IRenderEnvConsumer {

    /** Adds a context listener. */
    void addContextListener(IContextListener contextListener);

    /** Removes a previously added context listener. */
    void removeContextListener(IContextListener contextListener);

    /**
     * @return {@code true} if this context is active. Only active contexts are updated.
     */
    boolean isActive();

    /** Update's the context's screen for this frame. */
    void updateScreen();

    /** Runs the context's scripts for this frame. */
    void updateScripts();

    /** Callback for when this context is made current (only the current context can run). */
    void onCurrent();

    /** Returns the context's screen. */
    IScreen getScreen();

    /** Returns the scripting part of this context. */
    IScriptContext getScriptContext();

    /** Returns object responsible for managing the skipping state. */
    ISkipState getSkipState();

}
