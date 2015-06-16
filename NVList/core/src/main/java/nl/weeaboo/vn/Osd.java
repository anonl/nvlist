package nl.weeaboo.vn;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Dim;

final class Osd implements Disposable {

	private BitmapFont font;
	
	private Osd() {		
	}
	
	public static Osd newInstance() {
		Osd osd = new Osd();
		osd.init();
		return osd;
	}
	
	public void init() {
	    FileHandle fontFile = Gdx.files.internal("font/DejaVuSerif.ttf");
	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
	    try {
		    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		    parameter.flip = false;
		    parameter.size = 32;
		    parameter.color = Color.WHITE;
		    font = generator.generateFont(parameter);
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
	
	public void render(Batch batch, Dim vsize) {
		List<String> lines = Lists.newArrayList();
		lines.add("FPS: " + Gdx.graphics.getFramesPerSecond());
		
		font.draw(batch, Joiner.on('\n').join(lines), 0, font.getCapHeight(), vsize.w, Align.left, true);
	}
	
}
