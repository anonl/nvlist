package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.impl.lua.LuaScriptContext;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

@SuppressWarnings("serial")
public class TestContextFactory implements IContextFactory<Context> {

    public final LuaScriptEnv scriptEnv;

    public TestContextFactory(LuaScriptEnv scriptEnv) {
        this.scriptEnv = scriptEnv;
    }

    @Override
    public Context newContext() {
        ContextArgs contextArgs = new ContextArgs();
        contextArgs.screen = newScreen();
        contextArgs.scriptContext = newScriptContext();

        return new Context(contextArgs);
    }

    protected Screen newScreen() {
        return CoreTestUtil.newScreen();
    }

    protected LuaScriptContext newScriptContext() {
        if (scriptEnv == null) {
            return null;
        }
        return new LuaScriptContext(scriptEnv);
    }

    @Override
    public void setRenderEnv(IRenderEnv env) {
    }

}
