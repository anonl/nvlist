package nl.weeaboo.vn.impl.script;

import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptThread;

/**
 * Exception handler which throws an {@link AssertionError}.
 */
public enum ThrowingScriptExceptionHandler implements IScriptExceptionHandler {

    INSTANCE;

    @Override
    public void onScriptException(IScriptThread thread, Exception exception) {
        throw new AssertionError("Exception in script thread: " + thread, exception);
    }

}
