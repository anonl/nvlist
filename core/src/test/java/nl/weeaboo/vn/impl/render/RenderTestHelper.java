package nl.weeaboo.vn.impl.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.gdx.graphics.GdxShaderUtil;
import nl.weeaboo.vn.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.impl.render.TriangleGrid.TriangleGridLayer;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.IDrawTransform;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;

/**
 * Various utility functions for unit testing the rendering system.
 */
public class RenderTestHelper implements IDestructible {

    private static final Logger LOG = LoggerFactory.getLogger(RenderTestHelper.class);

    private final DrawBuffer drawBuffer;
    private final Viewport viewport;
    private final GLScreenRenderer renderer;

    public RenderTestHelper(IRenderEnv renderEnv) {
        drawBuffer = new DrawBuffer();
        viewport = createViewport(renderEnv);

        renderer = new GLScreenRenderer(renderEnv, new RenderStats());
        renderer.setProjectionMatrix(viewport.getCamera().combined);
    }

    private static Viewport createViewport(IRenderEnv renderEnv) {
        Viewport viewPort = new FitViewport(renderEnv.getWidth(), renderEnv.getHeight());
        GdxViewportUtil.setToOrtho(viewPort, renderEnv.getVirtualSize(), true);
        viewPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        return viewPort;
    }

    @Override
    public void destroy() {
        renderer.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return renderer.isDestroyed();
    }

    /** Clears the internal draw buffer. */
    public void reset() {
        drawBuffer.reset();
    }

    /**
     * Renders the contents of the draw buffer.
     */
    public void render() {
        renderer.render(drawBuffer);
    }

    /**
     * Returns a reference to the internal draw buffer.
     */
    public DrawBuffer getDrawBuffer() {
        return drawBuffer;
    }

    /**
     * Draws a textured rectangle to the draw buffer.
     * @see IDrawBuffer#drawQuad(IDrawTransform, int, ITexture, Area2D, Area2D)
     */
    public void drawQuad(ITexture tex, Area2D bounds) {
        drawQuad(tex, new DrawTransform(), bounds);
    }

    /**
     * Draws a textured rectangle to the draw buffer.
     * @see IDrawBuffer#drawQuad(IDrawTransform, int, ITexture, Area2D, Area2D)
     */
    public void drawQuad(ITexture tex, IDrawTransform transform, Area2D bounds) {
        drawBuffer.drawQuad(transform, 0xFFFFFFFF, tex, bounds, ITexture.DEFAULT_UV);
    }

    /**
     * @see #drawText(IDrawTransform, double, double, ITextLayout)
     */
    public void drawText(double dx, double dy, ITextLayout textLayout) {
        drawText(new DrawTransform(), dx, dy, textLayout);
    }

    /**
     * Draws some text to the draw buffer.
     * @see IDrawBuffer#drawText(IDrawTransform, double, double, ITextLayout, double)
     */
    public void drawText(IDrawTransform transform, double dx, double dy, ITextLayout textLayout) {
        drawBuffer.drawText(transform, dx, dy, textLayout, -1f);
    }

    /**
     * Draw a triangle grid to the draw buffer.
     */
    public void drawTriangleGrid(ITexture tex, Area2D bounds) {
        TriangleGrid grid = TriangleGrid.layout(new TriangleGridLayer(bounds, tex.getUV(), TextureWrap.CLAMP));
        drawTriangleGrid(grid, tex);
    }

    private void drawTriangleGrid(TriangleGrid grid, ITexture tex) {
        drawBuffer.drawCustom(new DrawTransform(), 0xFFFFFFFF, new IRenderLogic() {
            @Override
            public void render(IScreenRenderer r) {
                ShaderProgram shader = SpriteBatch.createDefaultShader();
                shader.begin();
                try {
                    GdxShaderUtil.setTexture(shader, 0, tex, "u_texture");
                    renderer.renderTriangleGrid(grid, shader);
                } finally {
                    shader.end();
                    shader.dispose();
                }
            }
        });
    }

    /**
     * Runs some custom rendering code that needs direct access to a {@link SpriteBatch}.
     */
    public void renderCustom(ISpriteBatchConsumer renderOp) {
        SpriteBatch batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        try {
            renderOp.render(batch);
        } finally {
            if (batch.isDrawing()) {
                LOG.error("Custom render code forgot to end rendering");
                batch.end();
            }
            batch.dispose();
        }
    }

    /**
     * Rendering operation.
     */
    public interface ISpriteBatchConsumer {

        /**
         * @param batch A sprite batch to render with. You must call {@link SpriteBatch#begin()} before you
         *        start rendering.
         */
        void render(SpriteBatch batch);

    }
}
