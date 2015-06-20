package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.ISystemEventHandler;
import nl.weeaboo.vn.script.ScriptException;

public class TestSystemEventHandler implements ISystemEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TestSystemEventHandler.class);

    @Override
    public void onExit() throws ScriptException {
        LOG.info("SystemEventHandler.onExit()");
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
        LOG.info("SystemEventHandler.onPrefsChanged()");
    }

}
