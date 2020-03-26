package nl.weeaboo.vn.script;

/**
 * Handles exceptions from script code.
 */
public interface IScriptExceptionHandler {

    /**
     * Called when script thread execution throws an exception.
     */
    void onScriptException(IScriptThread thread, Exception exception);

}
