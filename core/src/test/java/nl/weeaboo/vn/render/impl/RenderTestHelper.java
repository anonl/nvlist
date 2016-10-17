package nl.weeaboo.vn.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.gdx.graphics.GdxShaderUtil;
import nl.weeaboo.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.IRenderLogic;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.render.impl.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.scene.impl.Layer;

public class RenderTestHelper implements IDestructible {

    private final IRenderEnv renderEnv;
    private final DrawBuffer drawBuffer;
    private final GLScreenRenderer renderer;

    public RenderTestHelper(IRenderEnv renderEnv) {
        this.renderEnv = renderEnv;

        drawBuffer = new DrawBuffer();

        FitViewport viewPort = new FitViewport(renderEnv.getWidth(), renderEnv.getHeight());
        GdxViewportUtil.setToOrtho(viewPort, renderEnv.getVirtualSize(), true);
        viewPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewPort.apply();

        renderer = new GLScreenRenderer(renderEnv, new RenderStats());
        renderer.setProjectionMatrix(viewPort.getCamera().combined);
    }

    @Override
    public void destroy() {
        renderer.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return renderer.isDestroyed();
    }

    public void reset() {
        drawBuffer.reset();
    }

    public void startLayer() {
        int layerId = drawBuffer.reserveLayerIds(1);
        Layer layer = new Layer(null);
        layer.setBounds(0, 0, renderEnv.getWidth(), renderEnv.getHeight());
        drawBuffer.startLayer(layerId, layer);
    }

    public void render() {
        renderer.render(drawBuffer);
    }

    public DrawBuffer getDrawBuffer() {
        return drawBuffer;
    }

    public void drawQuad(ITexture tex, Area2D bounds) {
        drawQuad(tex, new DrawTransform(), bounds);
    }
    public void drawQuad(ITexture tex, DrawTransform transform, Area2D bounds) {
        drawBuffer.drawQuad(transform, 0xFFFFFFFF, tex, bounds, ITexture.DEFAULT_UV);
    }

    public void drawText(double dx, double dy, ITextLayout textLayout) {
        drawBuffer.drawText(new DrawTransform(), dx, dy, textLayout, -1f);
    }

    public void drawTriangleGrid(ITexture tex, Area2D bounds) {
        TriangleGrid grid = TriangleGrid.layout1(bounds, tex.getUV(), TextureWrap.CLAMP);
        drawTriangleGrid(grid, tex);
    }
    public void drawTriangleGrid(TriangleGrid grid, ITexture tex) {
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
