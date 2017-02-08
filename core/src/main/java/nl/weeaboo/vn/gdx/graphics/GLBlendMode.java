package nl.weeaboo.vn.gdx.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;

import nl.weeaboo.vn.core.BlendMode;

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

    /** Applies this blend mode to the given sprite batch. */
    public void apply(Batch batch) {
        if (this == DISABLED) {
            batch.disableBlending();
        } else {
            batch.enableBlending();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
    }

    /** Converts a blend mode from the public API to its {@link GLBlendMode} equivalent. */
    public static GLBlendMode from(BlendMode blendMode) {
        switch (blendMode) {
        case DEFAULT:
            return GLBlendMode.DEFAULT_PREMULT;
        case ADD:
            return GLBlendMode.ADD;
        default:
            return GLBlendMode.DISABLED;
        }
    }

}
