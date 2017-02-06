package nl.weeaboo.vn.script;

public interface IScriptExceptionHandler {

    /**
     * Called when script thread execution throws an exception.
     */
    void onScriptException(IScriptThread thread, Exception exception);

}
