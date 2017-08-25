package nl.weeaboo.vn.impl.core;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.scene.ScreenTextState;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.text.ITextModule;

public class ContextFactory implements IContextFactory<Context> {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final LuaScriptEnv scriptEnv;
    private final ITextModule textModule;

    private IRenderEnv renderEnv;

    public ContextFactory(LuaScriptEnv scriptEnv, ITextModule textModule, IRenderEnv renderEnv) {
        this.scriptEnv = scriptEnv;
        this.textModule = textModule;
        this.renderEnv = renderEnv;
    }

    @Override
    public Context newContext() {
        SkipState skipState = new SkipState();

        ContextArgs contextArgs = new ContextArgs();
        contextArgs.skipState = skipState;
        contextArgs.screen = newScreen(skipState);
        contextArgs.scriptContext = newScriptContext();

        return new Context(contextArgs);
    }

    protected Screen newScreen(ISkipState skipState) {
        Rect2D rect = Rect2D.of(0, 0, renderEnv.getWidth(), renderEnv.getHeight());
        ScreenTextState textBoxState = new ScreenTextState(textModule.getTextLog());
        return new Screen(rect, renderEnv, textBoxState, skipState);
    }

    protected @Nullable LuaScriptContext newScriptContext() {
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
