package nl.weeaboo.vn.script;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IContext;

/**
 * Context-specific part of a script environment. A context includes a set of threads and a way of storing
 * context-specific data.
 */
public interface IScriptContext extends Serializable {

    /**
     * Loads a script file in a new thread.
     * @throws ScriptException If loading fails.
     */
    IScriptThread loadScriptInNewThread(FilePath path) throws ScriptException;

    /**
     * Creates a new thread and adds it to the script context.
     * @throws ScriptException If thread creation fails.
     */
    IScriptThread createThread(IScriptFunction func) throws ScriptException;

    /**
     * Returns the main script thread.
     */
    IScriptThread getMainThread();

    /**
     * Returns a read-only collection containing the currently registered script threads.
     */
    Collection<? extends IScriptThread> getThreads();

    /** Runs every active thread once until they yield. */
    void updateThreads(IContext context, IScriptExceptionHandler exceptionHandler);

    /** Returns the event dispatcher which can be used to schedule tasks on the context's main thread */
    IScriptEventDispatcher getEventDispatcher();

}
