package nl.weeaboo.vn.core.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.LvnTestUtil;
import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.LayerRenderCommand;
import nl.weeaboo.vn.render.impl.QuadRenderCommand;
import nl.weeaboo.vn.render.impl.RenderCommand;

public class LayerTest extends AbstractEntityTest {

    private static final double EPSILON = LvnTestUtil.EPSILON;

    private final ILayerHolder layerHolderStub = new LayerHolderStub();

    @Test
    public void subLayers() {
        Layer rootLayer = new Layer(layerHolderStub, scene, pr);
        Assert.assertEquals(Collections.emptyList(), rootLayer.getSubLayers());
        Assert.assertFalse(rootLayer.containsLayer(null)); // Contains is null-safe

        // root -> sub
        Layer subLayer = rootLayer.createSubLayer();
        Assert.assertEquals(Arrays.asList(subLayer), rootLayer.getSubLayers());
        Assert.assertTrue(rootLayer.containsLayer(subLayer));

        // root -> sub -> subsub
        Layer subSubLayer = subLayer.createSubLayer();
        Assert.assertEquals(Arrays.asList(subLayer), rootLayer.getSubLayers());
        Assert.assertTrue(rootLayer.containsLayer(subLayer));
        Assert.assertTrue(rootLayer.containsLayer(subSubLayer)); // Contains is recursive
        Assert.assertEquals(Arrays.asList(subSubLayer), subLayer.getSubLayers());
        Assert.assertTrue(subLayer.containsLayer(subSubLayer));

        // Destroy sub, which should also destroy subsub
        subLayer.destroy();
        Assert.assertTrue(subLayer.isDestroyed());
        Assert.assertTrue(subSubLayer.isDestroyed());
        Assert.assertEquals(Collections.emptyList(), rootLayer.getSubLayers());
    }

    @Test
    public void testBounds() {
        Layer layer = new Layer(layerHolderStub, scene, pr);
        layer.setBounds(1, 2, 3, 4);
        LvnTestUtil.assertEquals(1, 2, 3, 4, layer.getBounds());
        layer.setPos(5, 6);
        layer.setSize(7, 8);
        LvnTestUtil.assertEquals(5, 6, 7, 8, layer.getBounds());
        layer.setX(-1);
        layer.setY(-2);
        layer.setWidth(5);
        layer.setHeight(6);
        LvnTestUtil.assertEquals(-1, -2, 5, 6, layer.getBounds());
        Assert.assertEquals(layer.getBounds().x, layer.getX(), EPSILON);
        Assert.assertEquals(layer.getBounds().y, layer.getY(), EPSILON);
        Assert.assertEquals(layer.getBounds().w, layer.getWidth(), EPSILON);
        Assert.assertEquals(layer.getBounds().h, layer.getHeight(), EPSILON);
    }

    @Test
    public void drawLayers() {
        Layer layer = new Layer(layerHolderStub, scene, pr);
        layer.setBounds(1, 2, 3, 4);
        layer.setZ((short)5);

        Layer subLayer1 = layer.createSubLayer();
        subLayer1.setZ((short)10);

        Layer subLayer2 = layer.createSubLayer();
        subLayer2.setZ((short)-10);

        // Draw to buffer
        DrawBuffer buffer = new DrawBuffer(pr);
        layer.draw(buffer);

        // Check generated draw commands
        LayerRenderCommand lrc = buffer.getRootLayerCommand();
        Assert.assertEquals(5, lrc.z);
        LvnTestUtil.assertEquals(1, 2, 3, 4, lrc.layerBounds);

        // Find draw commands for sub layers (in correct Z order)
        List<? extends RenderCommand> layerCommands = buffer.getLayerCommands(lrc.layerId);
        Assert.assertEquals(2, layerCommands.size());
        // Higher Z-coordinates are in the back and thus drawn first
        Assert.assertEquals(10, ((LayerRenderCommand)layerCommands.get(0)).z);
        Assert.assertEquals(-10, ((LayerRenderCommand)layerCommands.get(1)).z);

        // Make one sublayer invisible
        subLayer1.setVisible(false);
        buffer.reset();
        layer.draw(buffer);

        // The invisible layer is no longer drawn
        layerCommands = buffer.getLayerCommands(buffer.getRootLayerCommand().layerId);
        Assert.assertEquals(1, layerCommands.size());
        Assert.assertEquals(-10, ((LayerRenderCommand)layerCommands.get(0)).z);
    }

    @Test
    public void drawEntities() {
        Layer layer = new Layer(layerHolderStub, scene, pr);
        layer.setBounds(0, 0, 1280, 720);

        Entity entity1 = NvlTestUtil.newImage(pr, scene);
        TransformablePart trans1 = entity1.getPart(pr.transformable);
        trans1.setZ((short)1);
        trans1.setAlpha(0);
        layer.add(entity1);

        Entity entity2 = NvlTestUtil.newImage(pr, scene);
        TransformablePart trans2 = entity2.getPart(pr.transformable);
        trans2.setZ((short)2);
        layer.add(entity2);

        // Drawables with alpha == 0 are not drawn
        assertRendered(layer, entity2);

        // Drawables with visible == false are not drawn
        trans1.setAlpha(1);
        trans1.setVisible(false);
        assertRendered(layer, entity2);

        trans1.setVisible(true);
        assertRendered(layer, entity2, entity1);
    }

    @Test
    public void updateRenderEnv() {
        IRenderEnv renderEnv = NvlTestUtil.BASIC_ENV;

        Layer layer = new Layer(layerHolderStub, scene, pr);
        Layer subLayer = layer.createSubLayer();

        Entity image = NvlTestUtil.newImage(pr, scene);
        subLayer.add(image);
        Assert.assertEquals(null, subLayer.getRenderEnv()); // Initially null
        Assert.assertEquals(null, image.getPart(pr.drawable).getRenderEnv()); // Initially null

        // Recursively passes to sub-layers and sub-drawables within those layers
        layer.setRenderEnv(renderEnv);
        Assert.assertEquals(renderEnv, subLayer.getRenderEnv());
        Assert.assertEquals(renderEnv, image.getPart(pr.drawable).getRenderEnv());
    }

    private void assertRendered(Layer layer, Entity... entities) {
        // Generate draw commands
        DrawBuffer buffer = new DrawBuffer(pr);
        layer.draw(buffer);

        LayerRenderCommand lrc = buffer.getRootLayerCommand();
        List<? extends RenderCommand> cmd = buffer.getLayerCommands(lrc.layerId);

        Assert.assertEquals(entities.length, cmd.size());
        for (int n = 0; n < entities.length; n++) {
            TransformablePart transformable = entities[n].getPart(pr.transformable);
            Assert.assertEquals(transformable.getZ(), ((QuadRenderCommand)cmd.get(n)).z);
        }
    }

}
