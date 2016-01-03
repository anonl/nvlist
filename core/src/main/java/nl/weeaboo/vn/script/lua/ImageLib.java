package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class ImageLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final DefaultEnvironment env;

    public ImageLib(DefaultEnvironment env) {
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
    public Varargs getActiveLayer(Varargs args) {
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

        return LuaValue.NONE;
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

}
