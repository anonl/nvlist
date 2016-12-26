package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.gdx.graphics.GdxBitmapTweenRenderer;
import nl.weeaboo.gdx.graphics.GdxCrossFadeRenderer;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.image.IBitmapTweenRenderer;
import nl.weeaboo.vn.image.ICrossFadeRenderer;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.image.impl.CrossFadeConfig;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;

public class TweenLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public TweenLib(IEnvironment env) {
        super("Tween");

        this.env = env;
    }

    /**
     * Creates a new {@link ICrossFadeRenderer} instance.
     *
     * @param args
     *        <ol>
     *        <li>duration (in frames)
     *        <li>(optional) interpolator
     *        </ol>
     * @return A new sound object, or {@code nil} if the sound couldn't be loaded.
     */
    @ScriptFunction
    public Varargs crossFade(Varargs args) {
        double duration = args.checkdouble(1);
        IInterpolator interpolator = InterpolatorsLib.getInterpolator(args.arg(2), Interpolators.SMOOTH);

        IImageModule imageModule = env.getImageModule();

        CrossFadeConfig config = new CrossFadeConfig(duration);
        config.setInterpolator(interpolator);

        ICrossFadeRenderer renderer = new GdxCrossFadeRenderer(imageModule, config);
        return LuajavaLib.toUserdata(renderer, ICrossFadeRenderer.class);
    }

    /**
     * Creates a new {@link IBitmapTweenRenderer} instance.
     *
     * @param args
     *        <ol>
     *        <li>controlImage
     *        <li>duration (in frames)
     *        <li>(optional) range
     *        <li>(optional) interpolator
     *        </ol>
     * @return A new sound object, or {@code nil} if the sound couldn't be loaded.
     */
    @ScriptFunction
    public Varargs bitmapTween(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture controlTex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
        if (controlTex == null) {
            throw new ScriptException("Invalid control image: " + args.arg(1));
        }

        double duration = args.checkdouble(2);
        double range = args.checkdouble(3);
        IInterpolator interpolator = InterpolatorsLib.getInterpolator(args.arg(4), Interpolators.SMOOTH);

        ControlImage controlImage = new ControlImage(controlTex, false);
        BitmapTweenConfig config = new BitmapTweenConfig(duration, controlImage);
        config.setRange(range);
        config.setInterpolator(interpolator);

        IBitmapTweenRenderer renderer = new GdxBitmapTweenRenderer(imageModule, config);
        return LuajavaLib.toUserdata(renderer, IBitmapTweenRenderer.class);
    }

}
