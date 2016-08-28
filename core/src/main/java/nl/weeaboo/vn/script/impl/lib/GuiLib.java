package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class GuiLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public GuiLib(IEnvironment env) {
        super("Gui");

        this.env = env;
    }

    /**
     * Creates a new {@link IButton}.
     *
     * @param args
     *        <ol>
     *        <li>(optional) Parent layer
     *        <li>(optional) Background image filename
     *        </ol>
     * @return A button
     */
    @ScriptFunction
    public Varargs createButton(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaScriptUtil.getRootLayer();
        }

        IImageModule imageModule = env.getImageModule();
        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(2));

        IScriptContext scriptContext = LuaScriptUtil.getCurrentScriptContext();
        IButton button = imageModule.createButton(parentLayer, scriptContext);
        if (tex != null) {
            button.setTexture(ButtonViewState.DEFAULT, tex);
        } else {
            button.setText("ERROR");
        }
        return LuajavaLib.toUserdata(button, IButton.class);
    }

}
