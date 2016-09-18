package nl.weeaboo.vn.debug;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.res.DisposeUtil;
import nl.weeaboo.gdx.res.GdxFileSystem;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.gdx.GdxFontGenerator;
import nl.weeaboo.styledtext.gdx.GdxFontStore;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.styledtext.gdx.YDir;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;
import nl.weeaboo.vn.text.impl.TextRenderer;

public final class Osd implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(Osd.class);

    private final String fontPath = "font/RobotoSlab.ttf";
    private final PerformanceMetrics performanceMetrics = new PerformanceMetrics();

    private GdxFontStore fontStore;
    private TextRenderer textRenderer;
    private TextStyle smallStyle;

	private Osd() {
	}

    public static Osd newInstance(GdxFileSystem fileSystem) {
		Osd osd = new Osd();
        osd.init(fileSystem);
		return osd;
	}

    public void init(GdxFileSystem fileSystem) {
        FileHandle fontFile = fileSystem.resolve(fontPath);
        if (!fontFile.exists()) {
            LOG.warn("OSD font doesn't exist: " + fontPath);
            return;
        }

        fontStore = new GdxFontStore();
        textRenderer = new TextRenderer();

        GdxFontGenerator fontGenerator = new GdxFontGenerator();
        fontGenerator.setYDir(YDir.DOWN);
        try {
            TextStyle normal = new TextStyle("normal", EFontStyle.PLAIN, 16);
            fontStore.registerFont(fontGenerator.load(fontFile, normal));
            textRenderer.setDefaultStyle(normal);
        } catch (IOException ioe) {
            LOG.warn("Error loading 'normal' OSD font", ioe);
        }

        try {
            MutableTextStyle small = new MutableTextStyle("small", EFontStyle.PLAIN, 12);
            small.setOutlineSize(.5f);
            small.setOutlineColor(0xFFFFFFFF);
            smallStyle = small.immutableCopy();
            fontStore.registerFont(fontGenerator.load(fontFile, smallStyle));
        } catch (IOException ioe) {
            LOG.warn("Error loading 'small' OSD font", ioe);
        }
	}

	@Override
	public void dispose() {
	    fontStore = DisposeUtil.dispose(fontStore);
	}

    public void render(Batch batch, IEnvironment env) {
        IRenderEnv renderEnv = env.getRenderEnv();
        Dim vsize = renderEnv.getVirtualSize();
        int pad = Math.min(vsize.w, vsize.h) / 64;
        int wrapWidth = vsize.w - pad * 2;

        MutableStyledText text = new MutableStyledText();
        text.setStyle(smallStyle);
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
                String srcloc = LuaScriptUtil.getNearestLvnSrcloc(mainThread.getStackTrace());
                if (srcloc != null) {
                    text.append("\n" + srcloc);
                }
            }
        }

        IInput input = StaticEnvironment.INPUT.get();
        Vec2 pointerPos = input.getPointerPos(Matrix.identityMatrix());
        text.append("\nMouse: (" + Math.round(pointerPos.x) + ", " + Math.round(pointerPos.y) + ")");

        textRenderer.setMaxSize(wrapWidth, vsize.h - pad * 2);
        textRenderer.setText(text.immutableCopy());
        GdxFontUtil.draw(batch, textRenderer.getVisibleLayout(), pad, pad, -1);
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
