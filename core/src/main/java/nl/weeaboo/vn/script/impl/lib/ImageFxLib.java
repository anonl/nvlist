package nl.weeaboo.vn.script.impl.lib;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.lua2.luajava.CoerceLuaToJava;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.IOffscreenRenderTask;
import nl.weeaboo.vn.render.impl.fx.BlurTask;
import nl.weeaboo.vn.render.impl.fx.ColorMatrix;
import nl.weeaboo.vn.render.impl.fx.ColorMatrixTask;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;

public class ImageFxLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    // Precomputed constants for LuaString instances we sometimes need.
    private static final LuaValue S_TEX = LuaString.valueOf("tex");
    private static final LuaValue S_POS = LuaString.valueOf("pos");
    private static final LuaValue S_OVERWRITE = LuaString.valueOf("overwrite");

    private final IEnvironment env;

    public ImageFxLib(IEnvironment env) {
        super("ImageFx");

        this.env = env;
    }

    @ScriptFunction
    public Varargs crop(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
        if (tex == null) {
            return LuaNil.NIL;
        }

        double x = args.optdouble(2, 0.0);
        double y = args.optdouble(3, 0.0);
        double w = args.optdouble(4, tex.getWidth());
        double h = args.optdouble(5, tex.getHeight());

        TextureRegion cropped = GdxTextureUtil.getTextureRegion(tex, Area2D.of(x, y, w, h));
        if (cropped == null) {
            throw new ScriptException("Unsupported texture type: " + tex);
        }

        return LuajavaLib.toUserdata(cropped, ITexture.class);
    }

    private static Set<Direction> getDirectionsSet(Varargs args, int index, int defaultValue) {
        LuaValue val = args.arg(index);
        if (val.isboolean()) {
            if (val.toboolean()) {
                return EnumSet.of(Direction.CENTER);
            } else {
                return EnumSet.noneOf(Direction.class);
            }
        }

        EnumSet<Direction> result = EnumSet.noneOf(Direction.class);
        int intValue = val.optint(defaultValue);
        // Each digit of the int value represents a direction. Iterate over the digits.
        while (intValue > 0) {
            int digit = intValue % 10;

            result.add(Direction.fromInt(digit));

            intValue /= 10;
        }
        return result;
    }

    private void addOffscreenRenderTask(IOffscreenRenderTask task) throws ScriptException {
        IScreen currentScreen = ContextUtil.getCurrentScreen();
        if (currentScreen == null) {
            throw new ScriptException("No screen is current");
        }
        currentScreen.getOffscreenRenderTaskBuffer().add(task);
    }

    @ScriptFunction
    public Varargs blur(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
        if (tex == null) {
            return LuaNil.NIL;
        }
        double radius = args.optdouble(2, 8.0);
        Set<Direction> expandDirs = getDirectionsSet(args, 3, 2468);

        BlurTask task = new BlurTask(imageModule, tex, radius);
        task.setExpandDirs(expandDirs);
        addOffscreenRenderTask(task);
        return LuajavaLib.toUserdata(task, IOffscreenRenderTask.class);
    }

//    @ScriptFunction
//    public Varargs blurMultiple(Varargs args) {
//        IImageModule imageModule = env.getImageModule();
//
//        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
//        if (tex == null) {
//            return LuaNil.NIL;
//        }
//        int levels = args.optint(2, 1);
//        int k = args.optint(3, 8);
//        Set<Direction> extendDirs = getDirectionsSet(args, 4, 0);
//
//        IImageFxModule imageFxModule = env.getImageFxModule();
//        ITexture[] blurTexs = imageFxModule.blurMultiple(tex, 0, levels, k, extendDirs);
//
//        LuaTable table = new LuaTable(blurTexs.length, 0);
//        for (int n = 0; n < blurTexs.length; n++) {
//            table.rawset(1 + n, LuajavaLib.toUserdata(blurTexs[n], ITexture.class));
//        }
//        return table;
//    }

    @ScriptFunction
    public Varargs brighten(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
        if (tex == null) {
            return LuaNil.NIL;
        }
        double add = args.optdouble(2, 0.5);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setOffsets(new double[] {add, add, add, 0.0});

        ColorMatrixTask task = new ColorMatrixTask(imageModule, tex, matrix);
        addOffscreenRenderTask(task);
        return LuajavaLib.toUserdata(task, IOffscreenRenderTask.class);
    }

//    @ScriptFunction
//    public Varargs mipmap(Varargs args) {
//        IImageModule imageModule = env.getImageModule();
//
//        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
//        if (tex == null) {
//            return LuaNil.NIL;
//        }
//
//        int level = args.optint(2, 1);
//
//        IImageFxModule imageFxModule = env.getImageFxModule();
//        ITexture newTex = imageFxModule.mipmap(tex, level);
//        return LuajavaLib.toUserdata(newTex, ITexture.class);
//    }

    @ScriptFunction
    public Varargs colorMatrix(Varargs args) throws ScriptException {
        IImageModule imageModule = env.getImageModule();

        ITexture tex = LuaConvertUtil.getTextureArg(imageModule, args.arg(1));
        if (tex == null) {
            return LuaNil.NIL;
        }

        ColorMatrix matrix = new ColorMatrix();
        double[] offsets = new double[4];
        if (args.arg(2).istable()) {
            double[] arr = toDoubleArray(args.arg(2), 5);
            matrix.setRedFactors(arr);
            offsets[0] = arr[0];
        }
        if (args.arg(3).istable()) {
            double[] arr = toDoubleArray(args.arg(3), 5);
            matrix.setGreenFactors(arr);
            offsets[1] = arr[1];
        }
        if (args.arg(4).istable()) {
            double[] arr = toDoubleArray(args.arg(4), 5);
            matrix.setBlueFactors(arr);
            offsets[2] = arr[2];
        }
        if (args.arg(5).istable()) {
            double[] arr = toDoubleArray(args.arg(5), 5);
            matrix.setAlphaFactors(arr);
            offsets[3] = arr[3];
        }
        matrix.setOffsets(offsets);

        ColorMatrixTask task = new ColorMatrixTask(imageModule, tex, matrix);
        addOffscreenRenderTask(task);
        return LuajavaLib.toUserdata(task, IOffscreenRenderTask.class);
    }

    private static double[] toDoubleArray(LuaValue val, int length) {
        double[] values = CoerceLuaToJava.coerceArg(val, double[].class);
        return Arrays.copyOf(values, length);
    }

//    /**
//     * Expects a single argument of the form:
//     *
//     * <pre>
//     * {
//     *     {tex=myTexture1},
//     *     {tex=myTexture2, pos={10, 10}, overwrite=true},
//     *     {tex=myTexture3}
//     * }
//     * </pre>
//     *
//     * <p>
//     * The {@code pos} field is optional and assumed <code>{0, 0}</code> when omitted.
//     * <p>
//     * The {@code overwrite} field is also optional. Setting it to {@code true} makes the texture overwrite
//     * whatever's underneath it rather than doing proper (and much slower) alpha blending.
//     */
//    @ScriptFunction
//    public Varargs composite(Varargs args) {
//        IImageModule imageModule = env.getImageModule();
//
//        LuaTable table = args.opttable(1, new LuaTable());
//        double w = args.optdouble(2, -1);
//        double h = args.optdouble(3, -1);
//
//        ImageCompositor compositor = new ImageCompositor(w, h);
//
//        LuaValue v;
//        for (int n = 1; (v = table.get(n)) != LuaNil.NIL; n++) {
//            // tex
//            ITexture tex = LuaConvertUtil.getTextureArg(imageModule, v.get(S_TEX));
//            TextureCompositeInfo tci = new TextureCompositeInfo(tex);
//
//            // pos
//            LuaTable posT = v.get(S_POS).opttable(null);
//            if (posT != null) {
//                tci.setOffset(posT.todouble(1), posT.todouble(2));
//            }
//
//            // overwrite
//            tci.setOverwrite(v.get(S_OVERWRITE).toboolean());
//
//            // Add to list
//            infos.add(tci);
//        }
//
//        IImageFxModule imageFxModule = env.getImageFxModule();
//        ITexture newTex = imageFxModule.composite(w, h, infos);
//
//        return LuajavaLib.toUserdata(newTex, ITexture.class);
//    }

}
