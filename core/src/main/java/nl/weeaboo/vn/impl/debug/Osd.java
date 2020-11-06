package nl.weeaboo.vn.impl.debug;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.common.annotations.VisibleForTesting;
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
import nl.weeaboo.styledtext.StyledText;
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
import nl.weeaboo.vn.impl.text.TextUtil;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.text.ITextRenderer;

/**
 * On-screen display for debug mode (see {@link NovelPrefs#DEBUG}).
 */
public final class Osd {

    private final IPerformanceMetrics performanceMetrics;
    private final TextStyle defaultStyle;

    private boolean visible = false;

    public Osd(IPerformanceMetrics perfMetrics) {
        this.performanceMetrics = Checks.checkNotNull(perfMetrics);

        MutableTextStyle normalBuilder = new MutableTextStyle(TextUtil.DEFAULT_FONT_NAME, EFontStyle.PLAIN, 16);
        normalBuilder.setShadowColor(0xFF000000);
        normalBuilder.setShadowDx(.5f);
        normalBuilder.setShadowDy(.5f);
        defaultStyle = normalBuilder.immutableCopy();
    }

    /** Handle input and update internal state. */
    public void update(IEnvironment env, IInput input) {
        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        if (input.consumePress(VKey.TOGGLE_OSD)) {
            setVisible(!isVisible());
        }
    }

    /** Renders the on-screen display to the screen. If not visible, this is a no-op. */
    public void render(Batch batch, IEnvironment env) {
        render(batch, env, new TextRenderer(env.getTextModule().getFontStore()));
    }

    @VisibleForTesting
    void render(Batch batch, IEnvironment env, ITextRenderer textRenderer) {
        if (!visible) {
            return;
        }

        textRenderer.setDefaultStyle(defaultStyle);

        Dim vsize = env.getRenderEnv().getVirtualSize();
        int pad = Math.min(vsize.w, vsize.h) / 64;
        int wrapWidth = vsize.w - pad * 2;
        textRenderer.setMaxSize(wrapWidth, vsize.h - pad * 2);
        textRenderer.setText(getOsdText(env));

        batch.begin();
        try {
            GdxFontUtil.draw(batch, textRenderer.getVisibleLayout(), pad, pad, -1);
        } finally {
            batch.end();
        }
    }

    private StyledText getOsdText(IEnvironment env) {
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

        Rect rclip = env.getRenderEnv().getRealClip();
        text.append("\nResolution: [" + rclip.x + ", " + rclip.y + ", " + rclip.w + ", " + rclip.h + "]");

        IInput input = StaticEnvironment.INPUT.get();
        Vec2 pointerPos = input.getPointerPos(Matrix.identityMatrix());
        text.append("\nMouse: (" + Math.round(pointerPos.x) + ", " + Math.round(pointerPos.y) + ")");

        return text.immutableCopy();
    }

    private static void printLayers(List<String> out, int indent, ILayer layer) {
        String str = "Layer(" + layer.getWidth() + ", " + layer.getHeight() + "): "
                + Iterables.size(layer.getChildren());

        out.add(Strings.repeat(" ", indent) + "+ " + str);
        for (ILayer subLayer : layer.getSubLayers()) {
            printLayers(out, indent + 1, subLayer);
        }
    }

    /**
     * @return {@code true} if visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Change visibility of the OSD overlay.
     */
    public void setVisible(boolean show) {
        visible = show;
    }

}
