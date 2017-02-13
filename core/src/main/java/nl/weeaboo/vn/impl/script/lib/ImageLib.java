package nl.weeaboo.vn.impl.script.lib;

import com.google.common.collect.Iterables;

import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class ImageLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public ImageLib(IEnvironment env) {
        super("Image");

        this.env = env;
    }

    /**
     * Creates an image drawable and adds it to a layer.
     *
     * @param args
     *        <ol>
     *        <li>Layer to which the new image drawable should be added.
     *        <li>Texture of filename
     *        </ol>
     * @return The newly created image drawable.
     * @throws ScriptException If the input parameters are invalid.
     */
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

    /**
     * Creates a new sub-layer.
     *
     * @param args
     *        <ol>
     *        <li>Parent layer (or {@code null} to use the root layer).
     *        </ol>
     * @return The newly created sub-layer.
     * @throws ScriptException If the input parameters are invalid.
     */
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
     * @throws ScriptException If no screen is current.
     */
    @ScriptFunction
    public Varargs getRootLayer(Varargs args) throws ScriptException {
        return LuajavaLib.toUserdata(LuaScriptUtil.getRootLayer(), ILayer.class);
    }

    /**
     * @param args ignored
     * @throws ScriptException If no screen is current.
     */
    @ScriptFunction
    public Varargs getActiveLayer(Varargs args) throws ScriptException {
        return LuajavaLib.toUserdata(LuaScriptUtil.getActiveLayer(), ILayer.class);
    }

    /**
     * @param args The layer to make active
     * @throws ScriptException If no screen is current.
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
     * @return A table containing all the drawables in the layer.
     * @throws ScriptException If the input parameters are invalid.
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

    /**
     * Schedules a screenshot to be taken.
     *
     * @param args
     *        <ol>
     *        <li>layer in which to take the screenshot
     *        <li>(optional) number: z-index at which to take the screenshot during rendering.
     *        <li>(optional) boolean: Clipping mode. If {@code false}, takes a screenshot of the entire screen instead
     *        of just the layer.
     *        <li>(optional) boolean: Volatile pixel mode. If {@code true}, the screenshot is stored only in volatile
     *        GPU memory meaning its pixels may be lost at any time.
     *        <li>
     *        </ol>
     * @return A screenshot object that will be filled asynchronously the next time the screen is rendered.
     * @throws ScriptException If the input parameters are invalid.
     */
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
     * @throws ScriptException If the Lua value isn't convertible to a texture.
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
    public Varargs getBlankTexture(Varargs args) {
        return getColorTexture(0x00000000);
    }

    /**
     * @param args Not used
     * @return A 1x1 white texture object
     */
    @ScriptFunction
    public Varargs getWhiteTexture(Varargs args) {
        return getColorTexture(0xFFFFFFFF);
    }

    /**
     * Creates a solid-color texture.
     *
     * @param args
     *        <ol>
     *        <li>argb color
     *        </ol>
     * @return A texture.
     */
    @ScriptFunction
    public Varargs getColorTexture(Varargs args) {
        int colorARGB = args.checkint(1);
        return getColorTexture(colorARGB);
    }

    private Varargs getColorTexture(int argb) {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = imageModule.getColorTexture(argb);
        if (tex == null) {
            return LuaNil.NIL;
        }
        return LuajavaLib.toUserdata(tex, ITexture.class);
    }

}
