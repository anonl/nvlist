package nl.weeaboo.vn.script;

import java.io.Serializable;

/**
 * Java wrapper around a script function.
 */
public interface IScriptFunction extends Serializable {

    /**
     * Runs the script function.
     * @throws ScriptException If the script encounters a runtime exception.
     *
     * @deprecated Use {@link IScriptContext} to create threads or run code instead.
     */
    @Deprecated
    void call() throws ScriptException;

}
