package nl.weeaboo.vn.script;

import java.io.Serializable;

import nl.weeaboo.vn.core.IDestructible;

public interface IScriptThread extends Serializable, IDestructible {

	/**
	 * Runs the thread until it yields.
	 * @throws ScriptException If an exception occurs while trying to execute the thread.
	 */
	public void update() throws ScriptException;

    /** @deprecated Exists for backwards compatibility with NVList 3.x */
    @Deprecated
    public boolean isFinished();

	/**
	 * @return {@code true} if the thread has been started and not yet finished.
	 */
	public boolean isRunnable();

}
