package nl.weeaboo.vn.impl.render.fx;

import java.io.IOException;
import java.util.Set;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.render.OffscreenRenderTask;
import nl.weeaboo.vn.math.Vec2;

public final class BlurTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;

    private static final int[] INTRINSIC_KERNELS = {10, 50};

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final ITexture tex;
    private final double inputRadius;
    private final double scaledRadius;

    public BlurTask(IImageModule imageModule, ITexture tex, double r) {
        super(imageModule, tex);

        this.tex = tex;
        this.inputRadius = r;

        if (r >= 32) {
            // With these values the blur appears to be approximately seamless
            r *= 0.45;
            scale(0.25);
        } else if (r >= 2) {
            r *= 0.5;
            scale(0.5);
        }

        this.scaledRadius = r;
    }

    @Override
    protected Pixmap render(RenderContext context) throws IOException {
        Vec2 pixelSize = toUV(1f);

        ShaderProgram shader = null;
        PingPongFbo fbos = null;
        try {
            fbos = new PingPongFbo(context.outerSize);
            fbos.start();

            context.drawInitial(GdxTextureUtil.getTextureRegion(tex), null);

            shader = shaderStore.get().createShaderFromClasspath(getClass(), findBestShader(scaledRadius));

            // Horizontal
            shader.begin();
            shader.setUniformf("radius", (float)(scaledRadius * pixelSize.x), 0);
            context.draw(fbos.nextBlank(), shader);

            // Vertical
            shader.begin();
            shader.setUniformf("radius", 0, (float)(scaledRadius * pixelSize.y));
            context.draw(fbos.nextBlank(), shader);

            return fbos.stop();
        } finally {
            DisposeUtil.dispose(shader);
            DisposeUtil.dispose(fbos);
        }
    }

    private static String findBestShader(double radius) {
        int best = INTRINSIC_KERNELS[INTRINSIC_KERNELS.length - 1];
        for (int numSamples : INTRINSIC_KERNELS) {
            if (numSamples >= 2 * radius) {
                best = numSamples;
                break;
            }
        }
        return "blur" + best;
    }

    private Vec2 toUV(double pixels) {
        return new Vec2(pixels / tex.getWidth(), pixels / tex.getHeight());
    }

    /**
     * Adds padding to each direction in the set. This padding is needed to avoid clipping the blur.
     */
    public void setExpandDirs(Set<Direction> expandDirs) {
        double top = (Direction.containsTop(expandDirs) ? scaledRadius : 0);
        double right = (Direction.containsRight(expandDirs) ? scaledRadius : 0);
        double bottom = (Direction.containsBottom(expandDirs) ? scaledRadius : 0);
        double left = (Direction.containsLeft(expandDirs) ? scaledRadius : 0);

        setPadding(Insets2D.of(top, right, bottom, left), true);
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("%s[radius=%.1f]",
                getClass().getSimpleName(), inputRadius);
    }

}
