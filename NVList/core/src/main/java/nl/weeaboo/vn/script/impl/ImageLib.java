package nl.weeaboo.vn.script.impl;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.lua.LuaScriptUtil;

public class ImageLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final DefaultEnvironment env;

    public ImageLib(DefaultEnvironment env) {
        super("Image");

        this.env = env;
    }

    protected ILayer getLayerArg(Varargs args, int index) throws ScriptException {
        if (args.isuserdata(index)) {
            ILayer layer = args.touserdata(index, ILayer.class);
            if (layer != null) {
                return layer;
            }
        } else if (args.isnil(index)) {
            return null;
        }
        throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
    }

    private static ILayer getActiveLayer() {
        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();
        return screen.getActiveLayer();
    }

    private static ILayer getRootLayer() {
        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();
        return screen.getRootLayer();
    }

    protected ITexture getTextureArg(Varargs args, int index) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        if (args.isstring(index)) {
            // Texture filename
            ResourceLoadInfo loadInfo = LuaScriptUtil.createLoadInfo(args.tojstring(2));
            return imageModule.getTexture(loadInfo, false);
        } else if (args.isuserdata(2)) {
            // Texture or screenshot object
            Object obj = args.touserdata(2);
            if (obj instanceof ITexture) {
                return (ITexture)obj;
            } else if (obj instanceof IScreenshot) {
                IScreenshot ss = (IScreenshot)obj;
                if (!ss.isAvailable()) {
                    throw new ScriptException("Screenshot data isn't available yet");
                }
                return imageModule.createTexture(ss);
            } else {
                throw new ScriptException("Invalid arguments");
            }
        } else if (!args.isnil(2)) {
            throw new ScriptException("Invalid arguments");
        }
        return null;
    }

    @ScriptFunction
    public Varargs createImage(Varargs args) throws ScriptException {
        ILayer layer = getLayerArg(args, 1);
        if (layer == null) {
            layer = getActiveLayer();
        }

        IImageModule imageModule = env.getImageModule();
        BasicPartRegistry pr = env.getPartRegistry();

        Entity e = imageModule.createImage(layer);
        ImagePart imagePart = e.getPart(pr.image);

        ITexture tex = getTextureArg(args, 2);
        if (tex != null) {
            imagePart.setTexture(tex);
        }

        return LuajavaLib.toUserdata(e, Entity.class);
    }

    @ScriptFunction
    public Varargs createLayer(Varargs args) throws ScriptException {
        ILayer parentLayer = getLayerArg(args, 1);
        if (parentLayer == null) {
            parentLayer = getRootLayer();
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
        return LuajavaLib.toUserdata(getActiveLayer(), ILayer.class);
    }

    /**
     * @param args The layer to make active
     */
    @ScriptFunction
    public Varargs setActiveLayer(Varargs args) throws ScriptException {
        ILayer layer = getLayerArg(args, 1);
        if (layer == null) {
            throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
        }

        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();
        screen.setActiveLayer(layer);

        return LuaValue.NONE;
    }

}
