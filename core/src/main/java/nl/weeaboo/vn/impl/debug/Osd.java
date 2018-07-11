package nl.weeaboo.vn.impl.debug;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.stats.FileLine;
import nl.weeaboo.vn.impl.text.TextRenderer;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;

public final class Osd implements Disposable {

    private final PerformanceMetrics performanceMetrics;

    private TextRenderer textRenderer;
    private boolean visible = false;

    private Osd(PerformanceMetrics perfMetrics) {
        this.performanceMetrics = Checks.checkNotNull(perfMetrics);
    }

    /** Constructor function. */
    public static Osd newInstance(PerformanceMetrics perfMetrics) {
        Osd osd = new Osd(perfMetrics);
        osd.init();
        return osd;
    }

    private void init() {
        textRenderer = new TextRenderer();

        MutableTextStyle normalBuilder = new MutableTextStyle("normal", EFontStyle.PLAIN, 16);
        normalBuilder.setShadowColor(0xFF000000);
        normalBuilder.setShadowDx(.5f);
        normalBuilder.setShadowDy(.5f);
        TextStyle normal = normalBuilder.immutableCopy();

        textRenderer.setDefaultStyle(normal);
    }

    @Override
    public void dispose() {
    }

    /** Handle input and update internal state. */
    public void update(IEnvironment env, INativeInput input) {
        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        if (input.consumePress(KeyCode.F7)) {
            visible = !visible;
        }
    }

    /** Renders the on-screen display to the screen. If not visible, this is a no-op. */
    public void render(Batch batch, IEnvironment env) {
        if (!visible) {
            return;
        }

        IRenderEnv renv = env.getRenderEnv();
        final Dim vsize = renv.getVirtualSize();
        final int pad = Math.min(vsize.w, vsize.h) / 64;
        final int wrapWidth = vsize.w - pad * 2;

        MutableStyledText text = new MutableStyledText();
        text.append(performanceMetrics.getPerformanceSummary());

        for (IContext active : env.getContextManager().getActiveContexts()) {
            ISkipState skipState = active.getSkipState();
            if (skipState.isSkipping()) {
                text.append("\nSkipping: " + skipState.getSkipMode());
            }

            List<String> layers = Lists.newArrayList();
            IScreen screen = active.getScreen();
            if (screen != null) {
                printLayers(layers, 0, screen.getRootLayer());
            }
            text.append("\n" + Joiner.on('\n').join(layers));

            IScriptContext scriptContext = active.getScriptContext();
            IScriptThread mainThread = scriptContext.getMainThread();
            if (mainThread != null) {
                FileLine srcloc = LuaScriptUtil.getNearestLvnSrcloc(mainThread.getStackTrace());
                if (srcloc != null) {
                    text.append("\n" + srcloc);
                }
            }
        }

        Rect rclip = renv.getRealClip();
        text.append("\nResolution: [" + rclip.x + ", " + rclip.y + ", " + rclip.w + ", " + rclip.h + "]");

        IInput input = StaticEnvironment.INPUT.get();
        Vec2 pointerPos = input.getPointerPos(Matrix.identityMatrix());
        text.append("\nMouse: (" + Math.round(pointerPos.x) + ", " + Math.round(pointerPos.y) + ")");

        textRenderer.setMaxSize(wrapWidth, vsize.h - pad * 2);
        textRenderer.setText(text.immutableCopy());

        batch.begin();
        try {
            GdxFontUtil.draw(batch, textRenderer.getVisibleLayout(), pad, pad, -1);
        } finally {
            batch.end();
        }
    }

    private static void printLayers(List<String> out, int indent, ILayer layer) {
        String str = "Layer(" + layer.getWidth() + ", " + layer.getHeight() + "): "
                + Iterables.size(layer.getChildren());

        out.add(Strings.repeat(" ", indent) + "+ " + str);
        for (ILayer subLayer : layer.getSubLayers()) {
            printLayers(out, indent + 1, subLayer);
        }
    }

}
