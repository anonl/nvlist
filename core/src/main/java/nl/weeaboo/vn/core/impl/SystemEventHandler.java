package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;

import nl.weeaboo.common.Checks;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.ISystemEventHandler;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class SystemEventHandler implements ISystemEventHandler {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SystemEventHandler.class);

    private final IContextManager contextManager;

    public SystemEventHandler(IContextManager contextManager) {
        this.contextManager = Checks.checkNotNull(contextManager);
    }

    @Override
    public void onExit() {
        LOG.info("SystemEventHandler.onExit()");
        if (!call("onExit")) {
            Gdx.app.exit();
        }
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
        LOG.info("SystemEventHandler.onPrefsChanged()");
        call("onPrefsChanged");
    }

    private boolean call(String functionName) {
        IContext context = contextManager.getPrimaryContext();
        try {
            LuaScriptUtil.callFunction(context, functionName);
            return true;
        } catch (Exception e) {
            LOG.warn("Exception while calling event handler: " + functionName, e);
            return false;
        }
    }

}
