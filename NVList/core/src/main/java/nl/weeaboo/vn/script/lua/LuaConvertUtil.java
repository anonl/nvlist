package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.Varargs;

import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.script.ScriptException;

final class LuaConvertUtil {

    public static ILayer getLayerArg(Varargs args, int index) throws ScriptException {
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

    public static ILayer getActiveLayer() {
        return ContextUtil.getCurrentScreen().getActiveLayer();
    }

    public static ILayer getRootLayer() {
        return ContextUtil.getCurrentScreen().getRootLayer();
    }

    public static ITexture getTextureArg(IImageModule imageModule, Varargs args, int index)
            throws ScriptException {

        if (args.isstring(index)) {
            // Texture filename
            ResourceLoadInfo loadInfo = LuaScriptUtil.createLoadInfo(args.tojstring(index));
            return imageModule.getTexture(loadInfo, false);
        } else if (args.isuserdata(index)) {
            // Texture or screenshot object
            Object obj = args.touserdata(index);
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
        } else if (!args.isnil(index)) {
            throw new ScriptException("Invalid arguments");
        }
        return null;
    }

}
