package nl.weeaboo.vn.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;

import nl.weeaboo.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.core.IRenderEnv;
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

}
