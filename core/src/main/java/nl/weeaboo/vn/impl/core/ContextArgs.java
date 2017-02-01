package nl.weeaboo.vn.impl.core;

import java.io.Serializable;

import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;

public final class ContextArgs implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    public Screen screen;
    public LuaScriptContext scriptContext;
    public ISkipState skipState;

}
