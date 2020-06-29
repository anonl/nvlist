package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.render.DisplayMode;
import nl.weeaboo.vn.render.IRenderEnv;

/**
 * Default implementation of {@link ISystemModule}.
 */
public class SystemModule extends AbstractModule implements ISystemModule {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SystemModule.class);

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final StaticRef<ISystemEnv> systemEnv = StaticEnvironment.SYSTEM_ENV;
    private final IEnvironment env;

    private transient @Nullable Dim windowedSize;

    public SystemModule(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        initTransients();
    }

    private void initTransients() {
        windowedSize = Dim.of(800, 600);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public ISystemEnv getSystemEnv() {
        return systemEnv.get();
    }

    @Override
    public void exit(boolean force) {
        LOG.info("SystemEventHandler.exit({})", force);

        if (force || !callFunction(KnownScriptFunctions.ON_EXIT)) {
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
        super.onPrefsChanged(config);

        LOG.info("SystemEventHandler.onPrefsChanged()");

        callFunction(KnownScriptFunctions.ON_PREFS_CHANGE);
    }

    @Override
    public void setRenderEnv(IRenderEnv env) {
        super.setRenderEnv(env);

        if (getSystemEnv().getDisplayMode() == DisplayMode.WINDOWED) {
            windowedSize = env.getScreenSize();
        }
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        if (!getSystemEnv().isDisplayModeSupported(mode)) {
            throw new IllegalStateException("Display mode isn't supported: " + mode);
        }

        boolean ok = false;
        switch (mode) {
        case FULL_SCREEN:
            LOG.debug("Switch to full-screen mode");
            ok = Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            break;
        case WINDOWED:
            LOG.debug("Switch to windowed mode: {}x{}", windowedSize.w, windowedSize.h);
            ok = Gdx.graphics.setWindowedMode(windowedSize.w, windowedSize.h);
            break;
        }

        if (!ok) {
            throw new IllegalStateException("Changing the display mode to " + mode + " failed");
        }
    }

    protected boolean callFunction(String functionName) {
        return callFunction(env, functionName);
    }

    static boolean callFunction(IEnvironment env, String functionName) {
        IContext context = env.getContextManager().getPrimaryContext();
        try {
            LuaScriptUtil.callFunction(context, functionName);
            return true;
        } catch (Exception e) {
            LOG.warn("Exception while calling event handler: " + functionName, e);
            return false;
        }
    }

}
