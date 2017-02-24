package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.scene.ButtonImageLoader;
import nl.weeaboo.vn.impl.scene.GridPanel;
import nl.weeaboo.vn.impl.scene.Viewport;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IGridPanel;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IViewport;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

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
     * @throws ScriptException If button creation fails.
     */
    @ScriptFunction
    public Varargs createButton(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaScriptUtil.getRootLayer();
        }

        IScriptContext scriptContext = LuaScriptUtil.getCurrentScriptContext();
        IImageModule imageModule = env.getImageModule();
        IButton button = imageModule.createButton(parentLayer, scriptContext);

        // TODO: Consider moving this block of code to a named function
        LuaValue imageArg = args.arg(2);
        if (imageArg.isstring()) {
            ResourceLoadInfo loadInfo = LuaConvertUtil.getLoadInfo(imageArg);
            ButtonImageLoader imageLoader = new ButtonImageLoader(imageModule);
            imageLoader.loadImages(button, loadInfo);
        } else {
            ITexture tex = LuaConvertUtil.getTextureArg(imageModule, imageArg);
            if (tex != null) {
                button.setTexture(ButtonViewState.DEFAULT, tex);
            } else {
                button.setText("ERROR");
            }
        }

        return LuajavaLib.toUserdata(button, IButton.class);
    }

    /**
     * Creates a new {@link IGridPanel}.
     *
     * @param args
     *        <ol>
     *        <li>(optional) Parent layer
     *        </ol>
     * @return A button
     * @throws ScriptException If panel creation fails.
     */
    @ScriptFunction
    public Varargs createGridPanel(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaScriptUtil.getRootLayer();
        }

        GridPanel panel = new GridPanel();
        panel.setUnscaledSize(parentLayer.getWidth(), parentLayer.getHeight());
        parentLayer.add(panel);

        return LuajavaLib.toUserdata(panel, IGridPanel.class);
    }

    /**
     * Creates a new {@link IViewport}.
     *
     * @param args
     *        <ol>
     *        <li>(optional) Parent layer
     *        </ol>
     * @return A viewport
     * @throws ScriptException If creation fails.
     */
    @ScriptFunction
    public Varargs createViewport(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaScriptUtil.getRootLayer();
        }

        Viewport viewport = new Viewport();
        viewport.setSize(parentLayer.getWidth(), parentLayer.getHeight());
        parentLayer.add(viewport);

        return LuajavaLib.toUserdata(viewport, IViewport.class);
    }

}
