package nl.weeaboo.vn.script;

public interface IScriptExceptionHandler {

    void onScriptException(IScriptThread thread, Exception exception);

}
