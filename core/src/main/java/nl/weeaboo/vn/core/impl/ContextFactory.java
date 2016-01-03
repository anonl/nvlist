package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;

public class ContextFactory implements IContextFactory<Context> {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    public final LuaScriptEnv scriptEnv;

    private IRenderEnv renderEnv;

    public ContextFactory(LuaScriptEnv scriptEnv, IRenderEnv renderEnv) {
        this.scriptEnv = scriptEnv;
        this.renderEnv = renderEnv;
    }

    protected Screen newScreen() {
        Rect2D rect = Rect2D.of(0, 0, renderEnv.getWidth(), renderEnv.getHeight());
        return new Screen(rect, renderEnv);
    }

    @Override
    public Context newContext() {
        ContextArgs contextArgs = new ContextArgs();
        contextArgs.screen = newScreen();
        contextArgs.scriptContext = newScriptContext();

        return new Context(contextArgs);
    }

    protected IScriptContext newScriptContext() {
        if (scriptEnv == null) {
            return null;
        }
        return new LuaScriptContext(scriptEnv);
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

}
