package nl.weeaboo.vn.script;

/**
 * Container for global scripting-related state shared between all {@link IScriptContext} objects.
 */
public interface IScriptEnv {

	public IScriptLoader getScriptLoader();

}
