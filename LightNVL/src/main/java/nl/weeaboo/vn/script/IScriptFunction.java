package nl.weeaboo.vn.script;

import java.io.Serializable;

public interface IScriptFunction extends Serializable {

	public void call() throws ScriptException;

}
