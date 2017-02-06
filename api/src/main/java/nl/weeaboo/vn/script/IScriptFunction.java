package nl.weeaboo.vn.script;

import java.io.Serializable;

public interface IScriptFunction extends Serializable {

    /**
     * Runs the script function.
     * @throws ScriptException If the script encounters a runtime exception.
     */
    void call() throws ScriptException;

}
