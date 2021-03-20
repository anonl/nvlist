package nl.weeaboo.vn.script;

import java.io.Serializable;

/**
 * Container for global scripting-related state shared between all {@link IScriptContext} objects.
 */
public interface IScriptEnv extends Serializable {

    /**
     * Returns the loader used for reading additional script files.
     */
    IScriptLoader getScriptLoader();

    /**
     * This exception handler is called whenever an exception is thrown from Lua code.
     */
    void setExceptionHandler(IScriptExceptionHandler exceptionHandler);

}
