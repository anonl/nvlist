package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.impl.lua.LuaScriptContext;

public final class ContextArgs implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    public Screen screen;
    public LuaScriptContext scriptContext;

}
