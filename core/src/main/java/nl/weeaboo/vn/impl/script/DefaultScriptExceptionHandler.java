package nl.weeaboo.vn.impl.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptThread;

public enum DefaultScriptExceptionHandler implements IScriptExceptionHandler {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultScriptExceptionHandler.class);

    @Override
    public void onScriptException(IScriptThread thread, Exception exception) {
        LOG.warn("Exception while executing thread: {}", thread, exception);
    }

}
