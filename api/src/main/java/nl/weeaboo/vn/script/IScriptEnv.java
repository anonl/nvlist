package nl.weeaboo.vn.script;

import java.io.Serializable;

/**
 * Container for global scripting-related state shared between all {@link IScriptContext} objects.
 */
public interface IScriptEnv extends Serializable {

	public IScriptLoader getScriptLoader();

}
