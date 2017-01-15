package nl.weeaboo.vn.script;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for scheduling pieces of code to run on the main thread of a script context.
 */
public interface IScriptEventDispatcher extends Serializable {

    /**
     * Enqueues an event
     */
    public void addEvent(IScriptFunction func);

    /**
     * Registers a function that will be called every frame until it is removed with
     * {@linkplain #removeTask(IScriptFunction)}.
     *
     * @param task The function to call.
     * @param priority The relative priority of the task. Active tasks are scheduled in order of descending
     *        priority.
     */
    public void addTask(IScriptFunction task, double priority);

    /**
     * Cancels all active tasks for the given script function.
     *
     * @return {@code true} if the task was found and removed, {@code false} otherwise.
     * @see #addTask(IScriptFunction, double)
     */
    public boolean removeTask(IScriptFunction function);

    /**
     * This method should be called once every frame.
     *
     * @return A collection of script functions that should be executed this frame.
     */
    public List<IScriptFunction> retrieveWork();

}
