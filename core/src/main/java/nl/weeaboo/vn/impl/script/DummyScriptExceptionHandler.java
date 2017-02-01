package nl.weeaboo.vn.impl.script;

import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptThread;

/** No-op implementation of {@link IScriptExceptionHandler} */
public enum DummyScriptExceptionHandler implements IScriptExceptionHandler {

    INSTANCE;

    @Override
    public void onScriptException(IScriptThread thread, Exception exception) {
    }

}
