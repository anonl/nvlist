package nl.weeaboo.vn.impl.core;

import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;

final class ContextArgs {

    Screen screen;
    LuaScriptContext scriptContext;
    ISkipState skipState;

}
