package nl.weeaboo.vn.script.impl;

import org.luaj.vm2.Varargs;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IScreen;
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
        }
        throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
    }

    protected ITexture getTextureArg(Varargs args, int index) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        if (args.isstring(index)) {
            // Texture filename
            String[] callStack = LuaScriptUtil.getLuaStack();
            return imageModule.getTexture(args.tojstring(2), callStack, false);
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

        IContext context = ContextUtil.getCurrentContext();
        IScreen screen = context.getScreen();

        ILayer layer = screen.createLayer(parentLayer);
        return LuajavaLib.toUserdata(layer, layer.getClass());
    }

}
