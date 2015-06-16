package nl.weeaboo.vn.script;

import java.util.List;

/**
 * Interface for scheduling pieces of code to run on the main thread of a script context.
 */
public interface IScriptEventDispatcher {

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
	 * Cancels an active task.
	 * @return {@code true} if the task was found and removed, {@code false} otherwise.
	 * @see #addTask(IScriptFunction, double)
	 */
	public boolean removeTask(IScriptFunction task);

	/**
	 * Removes all enqueued events
	 */
	public void clear();

	/**
	 * @return {@code true} if no events are currently enqueued
	 */
	public boolean isEmpty();

	/**
	 * This method should be called once every frame.
	 *
	 * @return A collection of script functions that should be executed this frame.
	 */
	public List<IScriptFunction> retrieveWork();

}
