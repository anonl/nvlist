package nl.weeaboo.vn.script;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.vn.core.IContext;

/**
 * Context-specific part of a script environment. A context includes a set of threads and a way of storing
 * context-specific data.
 */
public interface IScriptContext extends Serializable {

    IScriptThread newThread(IScriptFunction func) throws ScriptException;

    IScriptThread getMainThread();

    Collection<? extends IScriptThread> getThreads();

	/** Runs every active thread once until they yield. */
    void updateThreads(IContext context);

	/** Returns the event dispatcher which can be used to schedule tasks on the context's main thread */
    IScriptEventDispatcher getEventDispatcher();

}
