package nl.weeaboo.vn.render.impl.fx;

import java.io.IOException;
import java.util.Set;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.res.DisposeUtil;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.ShaderStore;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.impl.OffscreenRenderTask;

public final class BlurTask extends OffscreenRenderTask {

    private static final long serialVersionUID = 1L;

    private final StaticRef<ShaderStore> shaderStore = StaticEnvironment.SHADER_STORE;

    private final ITexture tex;
    private final int radius;

    public BlurTask(IImageModule imageModule, ITexture tex, int radius) {
        super(imageModule, tex);

        this.tex = tex;

        double r = radius;
        while (r > 5) {
            r *= 0.5;
            scale(0.5);
        }

        // TODO: Blur shader had a fixed radius. Create a few different kernel sizes.
        // Current blur impl has a fixed radius of 5.0
        this.radius = Math.max(5, (int)Math.round(r));
    }

    @Override
    protected Pixmap render(RenderContext context) throws IOException {
        Vec2 pixelSize = toUV(1f);

        ShaderProgram shader = null;
        PingPongFbo fbos = null;
        try {
            fbos = new PingPongFbo(context.outerSize);
            fbos.start();

            context.drawInitial(GdxTextureUtil.getTextureRegion(tex));

            shader = shaderStore.get().createShaderFromClasspath(getClass(), "blur");
            shader.begin();

            // Horizontal
            shader.setUniformf("radius", (float)pixelSize.x, 0);
            context.draw(fbos.nextBlank(), shader);

            // Vertical
            shader.begin();
            shader.setUniformf("radius", 0, (float)pixelSize.y);
            context.draw(fbos.nextBlank(), shader);

            return fbos.stop();
        } finally {
            DisposeUtil.dispose(shader);
            DisposeUtil.dispose(fbos);
        }
    }

    private Vec2 toUV(double pixels) {
        return new Vec2(pixels / tex.getWidth(), pixels / tex.getHeight());
    }

    public void setExpandDirs(Set<Direction> expandDirs) {
        int top = (Direction.containsTop(expandDirs) ? radius : 0);
        int right = (Direction.containsRight(expandDirs) ? radius : 0);
        int bottom = (Direction.containsBottom(expandDirs) ? radius : 0);
        int left = (Direction.containsLeft(expandDirs) ? radius : 0);

        setPadding(Insets2D.of(top, right, bottom, left), true);
    }

}
