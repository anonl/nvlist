package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;

public class ImageLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public ImageLib(IEnvironment env) {
        super("Image");

        this.env = env;
    }

    @ScriptFunction
    public Varargs createImage(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            layer = LuaConvertUtil.getActiveLayer();
        }

        IImageModule imageModule = env.getImageModule();

        IImageDrawable image = imageModule.createImage(layer);

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args, 2);
        if (tex != null) {
            image.setTexture(tex);
        }

        return LuajavaLib.toUserdata(image, IImageDrawable.class);
    }

    @ScriptFunction
    public Varargs createLayer(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaConvertUtil.getRootLayer();
        }

        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();

        ILayer layer = screen.createLayer(parentLayer);
        return LuajavaLib.toUserdata(layer, ILayer.class);
    }

    /**
     * @param args ignored
     */
    @ScriptFunction
    public Varargs getActiveLayer(Varargs args) throws ScriptException {
        return LuajavaLib.toUserdata(LuaConvertUtil.getActiveLayer(), ILayer.class);
    }

    /**
     * @param args The layer to make active
     */
    @ScriptFunction
    public Varargs setActiveLayer(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
        }

        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();
        screen.setActiveLayer(layer);

        return LuaConstants.NONE;
    }

    @ScriptFunction
    public Varargs screenshot(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            layer = LuaConvertUtil.getRootLayer();
        }
        int z = args.optint(2, Short.MIN_VALUE);
        boolean clip = args.optboolean(3, true);
        boolean isVolatile = args.optboolean(4, false);

        IImageModule imageModule = env.getImageModule();
        IScreenshot ss = imageModule.screenshot(layer, (short)z, isVolatile, clip);
        return LuajavaLib.toUserdata(ss, IScreenshot.class);

    }

    /**
     * @param args Any number of string arguments representing filenames of images to load.
     */
    @ScriptFunction
    public Varargs preload(Varargs args) {
        IImageModule imageModule = env.getImageModule();
        for (int n = 1; n <= args.narg(); n++) {
            imageModule.preload(args.tojstring(n));
        }
        return LuaConstants.NONE;
    }

}
