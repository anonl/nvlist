package nl.weeaboo.vn.impl.script.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.gdx.graphics.GdxBitmapTweenRenderer;
import nl.weeaboo.vn.gdx.graphics.GdxCrossFadeRenderer;
import nl.weeaboo.vn.image.IBitmapTweenConfig;
import nl.weeaboo.vn.image.IBitmapTweenRenderer;
import nl.weeaboo.vn.image.ICrossFadeConfig;
import nl.weeaboo.vn.image.ICrossFadeRenderer;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.impl.image.CrossFadeConfig;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class TweenLib extends LuaLib {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TweenLib.class);

    private final IEnvironment env;

    public TweenLib(IEnvironment env) {
        super("Tween");

        this.env = env;
    }

    /**
     * @param args
     *        <ol>
     *        <li>duration (in frames)
     *        </ol>
     * @return A new {@link ICrossFadeConfig} object.
     */
    @ScriptFunction
    public Varargs crossFadeConfig(Varargs args) {
        double duration = args.checkdouble(1);

        ICrossFadeConfig config = new CrossFadeConfig(duration);
        return LuajavaLib.toUserdata(config, ICrossFadeConfig.class);
    }

    /**
     * Creates a new {@link ICrossFadeRenderer} instance.
     *
     * @param args
     *        <ol>
     *        <li>config (see {@link #crossFadeConfig(Varargs)})
     *        </ol>
     */
    @ScriptFunction
    public Varargs crossFade(Varargs args) {
        CrossFadeConfig config = args.checkuserdata(1, CrossFadeConfig.class);

        IImageModule imageModule = env.getImageModule();

        ICrossFadeRenderer renderer = new GdxCrossFadeRenderer(imageModule, config);
        return LuajavaLib.toUserdata(renderer, ICrossFadeRenderer.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>duration (in frames)
     *        <li>control image (a texture)
     *        <li>tile control image (boolean)
     *        </ol>
     * @return A new {@link IBitmapTweenConfig} object.
     * @throws ScriptException If the input parameters are invalid.
     */
    @ScriptFunction
    public Varargs bitmapTweenConfig(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        double duration = args.checkdouble(1);
        ITexture controlTex = LuaConvertUtil.getTextureArg(imageModule, args.arg(2));
        if (controlTex == null) {
            LOG.warn("Invalid control image: {}, replacing with dummy", args.arg(2));
            controlTex = imageModule.getColorTexture(0xFF808080);
        }
        boolean tile = args.optboolean(3, false);

        ControlImage controlImage = new ControlImage(controlTex, tile);
        IBitmapTweenConfig config = new BitmapTweenConfig(duration, controlImage);
        return LuajavaLib.toUserdata(config, IBitmapTweenConfig.class);
    }

    /**
     * Creates a new {@link IBitmapTweenRenderer} instance.
     *
     * @param args
     *        <ol>
     *        <li>config (see {@link #bitmapTweenConfig(Varargs)})
     *        </ol>
     * @return A new sound object, or {@code nil} if the sound couldn't be loaded.
     */
    @ScriptFunction
    public Varargs bitmapTween(Varargs args) {
        IImageModule imageModule = env.getImageModule();

        BitmapTweenConfig config = args.checkuserdata(1, BitmapTweenConfig.class);

        IBitmapTweenRenderer renderer = new GdxBitmapTweenRenderer(imageModule, config);
        return LuajavaLib.toUserdata(renderer, IBitmapTweenRenderer.class);
    }

}
