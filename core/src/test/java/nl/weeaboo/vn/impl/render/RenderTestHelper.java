package nl.weeaboo.vn.impl.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;

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

public class RenderTestHelper implements IDestructible {

    private final DrawBuffer drawBuffer;
    private final GLScreenRenderer renderer;

    public RenderTestHelper(IRenderEnv renderEnv) {
        drawBuffer = new DrawBuffer();

        FitViewport viewPort = createViewport(renderEnv);

        renderer = new GLScreenRenderer(renderEnv, new RenderStats());
        renderer.setProjectionMatrix(viewPort.getCamera().combined);
    }

    private static FitViewport createViewport(IRenderEnv renderEnv) {
        FitViewport viewPort = new FitViewport(renderEnv.getWidth(), renderEnv.getHeight());
        GdxViewportUtil.setToOrtho(viewPort, renderEnv.getVirtualSize(), true);
        viewPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewPort.apply();
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
     * Draws some text to the draw buffer.
     * @see IDrawBuffer#drawText(IDrawTransform, double, double, ITextLayout, double)
     */
    public void drawText(double dx, double dy, ITextLayout textLayout) {
        drawBuffer.drawText(new DrawTransform(), dx, dy, textLayout, -1f);
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
            public void render(IScreenRenderer<?> r) {
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


}
