package nl.weeaboo.vn.script;

import java.io.Serializable;
import java.util.List;

import nl.weeaboo.vn.core.IDestructible;

/**
 * Java wrapper around a script thread.
 */
public interface IScriptThread extends Serializable, IDestructible {

    /**
     * Runs the thread until it yields.
     * @throws ScriptException If an exception occurs while trying to execute the thread.
     */
    void update() throws ScriptException;

    /**
     * Returns {@code true} if the thread has been started and not yet finished.
     */
    boolean isRunnable();

    /**
     * Returns the name of the thread (may not be unique).
     */
    String getName();

    /**
     * Returns a string representation of the current call stack of this thread. Returns an empty list if no
     *         stack trace is available.
     */
    List<String> getStackTrace();

    /**
     * Pauses execution of this thread until {@link #resume} is called.
     */
    void pause();

    /**
     * Unpauses execution of this thread after it was paused using {@link #pause}.
     */
    void resume();

}
