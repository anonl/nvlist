package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.Varargs;

import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
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

    private static IScreen getCurrentScreen() throws ScriptException {
        IScreen currentScreen = ContextUtil.getCurrentScreen();
        if (currentScreen == null) {
            throw new ScriptException("No screen active");
        }
        return currentScreen;
    }

    public static ILayer getActiveLayer() throws ScriptException {
        return getCurrentScreen().getActiveLayer();
    }

    public static ILayer getRootLayer() throws ScriptException {
        return getCurrentScreen().getRootLayer();
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
