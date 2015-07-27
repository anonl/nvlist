package nl.weeaboo.vn;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.lua.LuaScriptUtil;

final class Osd implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(Osd.class);

    private final String fontPath = "font/DejaVuSerif.ttf";

    private BitmapFont font;
    private BitmapFont smallFont;

	private Osd() {
	}

	public static Osd newInstance() {
		Osd osd = new Osd();
		osd.init();
		return osd;
	}

	public void init() {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        if (!fontFile.exists()) {
            LOG.warn("OSD font doesn't exist: " + fontPath);
            return;
        }

	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
	    try {
		    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		    parameter.flip = false;
		    parameter.size = 32;
		    parameter.color = Color.WHITE;
            parameter.borderColor = Color.WHITE;
            parameter.borderWidth = .5f;
		    font = generator.generateFont(parameter);

            parameter.size = 16;
            smallFont = generator.generateFont(parameter);
	    } finally {
	    	generator.dispose();
	    }
	}

	@Override
	public void dispose() {
		if (font != null) {
			font.dispose();
			font = null;
		}
	}

    public void render(Batch batch, IEnvironment env) {
        if (font == null) {
            return;
        }

		List<String> lines = Lists.newArrayList();
		lines.add("FPS: " + Gdx.graphics.getFramesPerSecond());

        IRenderEnv renderEnv = env.getRenderEnv();
        Dim vsize = renderEnv.getVirtualSize();
        int pad = Math.min(vsize.w, vsize.h) / 64;
        int wrapWidth = vsize.w - pad * 2;

        int y = vsize.h - pad;
        GlyphLayout layout = font.draw(batch, Joiner.on('\n').join(lines), pad, y, wrapWidth,
                Align.left, true);

        // Small text per context
        for (IContext active : env.getContextManager().getActiveContexts()) {
            List<String> layers = Lists.newArrayList();
            IScreen screen = active.getScreen();
            if (screen != null) {
                printLayers(layers, 0, screen.getRootLayer());
            }
            y -= layout.height + pad;
            layout = smallFont.draw(batch, Joiner.on('\n').join(layers), pad, y,
                    wrapWidth, Align.left, true);

            IScriptContext scriptContext = active.getScriptContext();
            IScriptThread mainThread = scriptContext.getMainThread();
            if (mainThread != null) {
                String srcloc = LuaScriptUtil.getNearestLvnSrcloc(mainThread.getStackTrace());
                if (srcloc != null) {
                    y -= layout.height + pad;
                    layout = smallFont.draw(batch, srcloc, pad, y, wrapWidth, Align.left, true);
                }
            }
        }
	}

    private static void printLayers(List<String> out, int indent, ILayer layer) {
        Collection<? extends ILayer> subLayers = layer.getSubLayers();

        String str = "Layer(" + layer.getWidth() + ", " + layer.getHeight() + "): "
                + Iterables.size(layer.getContents());
        out.add(Strings.repeat(" ", indent) + "+ " + str);
        if (!subLayers.isEmpty()) {
            for (ILayer subLayer : subLayers) {
                printLayers(out, indent + 1, subLayer);
            }
        }
    }

}
