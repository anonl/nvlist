package nl.weeaboo.vn.script.impl.lib;

import com.google.common.collect.Iterables;

import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class ImageLib extends LuaLib {

    private static final long serialVersionUID = 1L;
    private static final LuaString BLANK_TEX_PATH = LuaString.valueOf("blank");
    private static final LuaString WHITE_TEX_PATH = LuaString.valueOf("white");

    private final IEnvironment env;

    public ImageLib(IEnvironment env) {
        super("Image");

        this.env = env;
    }

    @ScriptFunction
    public Varargs createImage(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            layer = LuaScriptUtil.getActiveLayer();
        }

        IImageModule imageModule = env.getImageModule();

        IImageDrawable image = imageModule.createImage(layer);

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(2));
        if (tex != null) {
            image.setTexture(tex);
        }

        return LuajavaLib.toUserdata(image, IImageDrawable.class);
    }

    @ScriptFunction
    public Varargs createLayer(Varargs args) throws ScriptException {
        ILayer parentLayer = LuaConvertUtil.getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = LuaScriptUtil.getRootLayer();
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
    public Varargs getRootLayer(Varargs args) throws ScriptException {
        return LuajavaLib.toUserdata(LuaScriptUtil.getRootLayer(), ILayer.class);
    }

    /**
     * @param args ignored
     */
    @ScriptFunction
    public Varargs getActiveLayer(Varargs args) throws ScriptException {
        return LuajavaLib.toUserdata(LuaScriptUtil.getActiveLayer(), ILayer.class);
    }

    /**
     * @param args The layer to make active
     */
    @ScriptFunction
    public void setActiveLayer(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
        }

        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();
        screen.setActiveLayer(layer);
    }

    /**
     * Fetches a snaphot of all the drawables contained in a specific layer
     *
     * @param args The layer to get the drawables from.
     */
    @ScriptFunction
    public Varargs getDrawables(Varargs args) throws ScriptException {
        // We don't require the layer interface, IVisualGroup is good enough
        IVisualGroup layer = args.arg1().touserdata(IVisualGroup.class);
        if (layer == null) {
            throw new ScriptException("Invalid visual group arg: " + args.tojstring(1));
        }

        Iterable<IDrawable> drawables = Iterables.filter(layer.getChildren(), IDrawable.class);
        return CoerceJavaToLua.toTable(drawables, IDrawable.class);
    }

    @ScriptFunction
    public Varargs screenshot(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            layer = LuaScriptUtil.getRootLayer();
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
    public void preload(Varargs args) {
        IImageModule imageModule = env.getImageModule();
        for (int n = 1; n <= args.narg(); n++) {
            imageModule.preload(LuaConvertUtil.getPath(args, n));
        }
    }

    /**
     * @param args
     *        <ol>
     *        <li>filename
     *        <li>(optional) boolean suppressErrors
     *        </ol>
     * @return A texture object, or {@code null} if the requested texture couldn't be loaded.
     */
    @ScriptFunction
    public Varargs getTexture(Varargs args) throws ScriptException {
        boolean suppressErrors = args.toboolean(2);
        return getLuaTexture(args.arg(1), suppressErrors);
    }

    private Varargs getLuaTexture(LuaValue luaValue, boolean suppressErrors) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, luaValue, suppressErrors);
        if (tex == null) {
            return LuaNil.NIL;
        }
        return LuajavaLib.toUserdata(tex, ITexture.class);
    }

    /**
     * @param args Not used
     * @return A 1x1 transparent texture object
     */
    @ScriptFunction
    public Varargs getBlankTexture(Varargs args) throws ScriptException {
        return getLuaTexture(BLANK_TEX_PATH, false);
    }

    /**
     * @param args Not used
     * @return A 1x1 white texture object
     */
    @ScriptFunction
    public Varargs getWhiteTexture(Varargs args) throws ScriptException {
        return getLuaTexture(WHITE_TEX_PATH, false);
    }

    /**
     * Creates a solid-color texture with the requested dimensions.
     *
     * @param args
     *        <ol>
     *        <li>argb color
     *        <li>texture width
     *        <li>texture height
     *        </ol>
     * @return A texture.
     */
    @ScriptFunction
    public Varargs createColorTexture(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        int colorARGB = args.checkint(1);
        int w = args.toint(2);
        int h = args.toint(3);
        if (w < 1 || h < 1) {
            throw new ScriptException("Invalid dimensions (must be greater than zero): w=" + w + ", h=" + h);
        }

        ITexture tex = imageModule.createTexture(colorARGB, w, h, 1.0, 1.0);
        return LuajavaLib.toUserdata(tex, ITexture.class);
    }

}
