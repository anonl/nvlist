package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;

import nl.weeaboo.common.Checks;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class SystemModule implements ISystemModule {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SystemModule.class);

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final StaticRef<ISystemEnv> systemEnv = StaticEnvironment.SYSTEM_ENV;
    private final IEnvironment env;

    public SystemModule(IEnvironment env) {
        this.env = Checks.checkNotNull(env);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public void exit(boolean force) {
        LOG.info("SystemEventHandler.exit({})", force);

        if (force || !call(KnownScriptFunctions.ON_EXIT)) {
            doExit();
        }
    }

    protected void doExit() {
        Gdx.app.exit();
    }

    @Override
    public boolean canExit() {
        return getSystemEnv().canExit();
    }

    @Override
    public void restart() throws InitException {
        INovel novel = novelRef.getIfPresent();
        if (novel == null) {
            throw new InitException("Unable to restart, novel is null");
        }
        novel.restart();
    }

    @Override
    public void openWebsite(String url) {
        Gdx.net.openURI(url);
    }

    @Override
    public void onPrefsChanged(IPreferenceStore config) {
        LOG.info("SystemEventHandler.onPrefsChanged()");

        call(KnownScriptFunctions.ON_PREFS_CHANGE);
    }

    protected boolean call(String functionName) {
        IContextManager contextManager = env.getContextManager();
        IContext context = contextManager.getPrimaryContext();
        try {
            LuaScriptUtil.callFunction(context, functionName);
            return true;
        } catch (Exception e) {
            LOG.warn("Exception while calling event handler: " + functionName, e);
            return false;
        }
    }

    @Override
    public ISystemEnv getSystemEnv() {
        return systemEnv.get();
    }

}
