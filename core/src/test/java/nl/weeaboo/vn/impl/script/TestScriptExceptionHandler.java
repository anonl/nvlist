package nl.weeaboo.vn.impl.script;

import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptThread;

public enum TestScriptExceptionHandler implements IScriptExceptionHandler {

    INSTANCE;

    @Override
    public void onScriptException(IScriptThread thread, Exception exception) {
        throw new AssertionError("Exception in script thread: " + thread, exception);
    }

}
