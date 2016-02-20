package nl.weeaboo.gdx.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;

public enum GLBlendMode {

    DEFAULT(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA), // libGDX doesn't use premultiplied alpha!
    DEFAULT_PREMULT(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA), // Premultiplied version of DEFAULT
	ADD(GL20.GL_ONE, GL20.GL_ONE),
	DISABLED(0, 0);

	public final int srcFunc;
	public final int dstFunc;

	private GLBlendMode(int sfactor, int dfactor) {
		this.srcFunc = sfactor;
		this.dstFunc = dfactor;
	}

	public void apply(Batch batch) {
	    if (this == DISABLED) {
	        batch.disableBlending();
	    } else {
	        batch.enableBlending();
	        batch.setBlendFunction(srcFunc, dstFunc);
	    }
	}

}
