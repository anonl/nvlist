package nl.weeaboo.vn.impl.scene;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.impl.core.RenderEnv;
import nl.weeaboo.vn.impl.render.DrawBuffer;
import nl.weeaboo.vn.impl.render.LayerRenderCommand;
import nl.weeaboo.vn.impl.render.QuadRenderCommand;
import nl.weeaboo.vn.impl.render.RenderCommand;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IVisualElement;

public class LayerTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    @Test
    public void subLayers() {
        Layer rootLayer = new RootLayerStub();
        assertSubLayers(Collections.<ILayer>emptyList(), rootLayer);
        Assert.assertFalse(rootLayer.containsLayer(null)); // Contains is null-safe

        // root -> sub
        Layer subLayer = rootLayer.createSubLayer();
        assertSubLayers(Arrays.asList(subLayer), rootLayer);
        Assert.assertTrue(rootLayer.containsLayer(subLayer));

        // root -> sub -> subsub
        Layer subSubLayer = subLayer.createSubLayer();
        assertSubLayers(Arrays.asList(subLayer), rootLayer);
        Assert.assertTrue(rootLayer.containsLayer(subLayer));
        Assert.assertTrue(rootLayer.containsLayer(subSubLayer)); // Contains is recursive
        assertSubLayers(Arrays.asList(subSubLayer), subLayer);
        Assert.assertTrue(subLayer.containsLayer(subSubLayer));

        // Destroy sub, which should also destroy subsub
        subLayer.destroy();
        Assert.assertTrue(subLayer.isDestroyed());
        Assert.assertTrue(subSubLayer.isDestroyed());
        assertSubLayers(Collections.<ILayer>emptyList(), rootLayer);
    }

    @Test
    public void testBounds() {
        Layer layer = new RootLayerStub();
        layer.setBounds(1, 2, 3, 4);
        RectAssert.assertEquals(Rect2D.of(1, 2, 3, 4), layer.getBounds(), EPSILON);
        layer.setPos(5, 6);
        layer.setSize(7, 8);
        RectAssert.assertEquals(Rect2D.of(5, 6, 7, 8), layer.getBounds(), EPSILON);
        layer.setX(-1);
        layer.setY(-2);
        layer.setWidth(5);
        layer.setHeight(6);
        RectAssert.assertEquals(Rect2D.of(-1, -2, 5, 6), layer.getBounds(), EPSILON);
        Assert.assertEquals(layer.getBounds().x, layer.getX(), EPSILON);
        Assert.assertEquals(layer.getBounds().y, layer.getY(), EPSILON);
        Assert.assertEquals(layer.getBounds().w, layer.getWidth(), EPSILON);
        Assert.assertEquals(layer.getBounds().h, layer.getHeight(), EPSILON);
    }

    @Test
    public void drawLayers() {
        Layer layer = new RootLayerStub();
        layer.setBounds(1, 2, 3, 4);
        layer.setZ((short)5);

        Layer subLayer1 = layer.createSubLayer();
        subLayer1.setZ((short)10);

        Layer subLayer2 = layer.createSubLayer();
        subLayer2.setZ((short)-10);

        // Draw to buffer
        DrawBuffer buffer = new DrawBuffer();
        layer.draw(buffer);

        // Check generated draw commands
        LayerRenderCommand lrc = (LayerRenderCommand)Iterables.getOnlyElement(buffer.getCommands());
        Assert.assertEquals(5, lrc.z);
        RectAssert.assertEquals(Rect2D.of(1, 2, 3, 4), lrc.layerBounds, EPSILON);

        // Find draw commands for sub layers (in correct Z order)
        DrawBuffer layerBuffer = buffer.getLayerBuffer(lrc.layerId);
        List<? extends RenderCommand> layerCommands = layerBuffer.getCommands();
        Assert.assertEquals(2, layerCommands.size());
        // Higher Z-coordinates are in the back and thus drawn first
        Assert.assertEquals(10, ((LayerRenderCommand)layerCommands.get(0)).z);
        Assert.assertEquals(-10, ((LayerRenderCommand)layerCommands.get(1)).z);

        // Make one sublayer invisible
        subLayer1.setVisible(false);
        buffer.reset();
        layer.draw(buffer);

        // The invisible layer is no longer drawn
        layerBuffer = buffer.getLayerBuffer(lrc.layerId);
        layerCommands = layerBuffer.getCommands();
        Assert.assertEquals(1, layerCommands.size());
        Assert.assertEquals(-10, ((LayerRenderCommand)layerCommands.get(0)).z);
    }

    @Test
    public void drawEntities() {
        Layer layer = new RootLayerStub();
        layer.setBounds(0, 0, 1280, 720);

        IImageDrawable entity1 = CoreTestUtil.newImage();
        entity1.setZ((short)1);
        entity1.setAlpha(0);
        layer.add(entity1);

        IImageDrawable entity2 = CoreTestUtil.newImage();
        entity2.setZ((short)2);
        layer.add(entity2);

        // Drawables with alpha == 0 are not drawn
        assertRendered(layer, entity2);

        // Drawables with visible == false are not drawn
        entity1.setAlpha(1);
        entity1.setVisible(false);
        assertRendered(layer, entity2);

        entity1.setVisible(true);
        assertRendered(layer, entity2, entity1);
    }

    @Test
    public void updateRenderEnv() {
        IRenderEnv renderEnv = CoreTestUtil.BASIC_ENV;

        RootLayerStub layer = new RootLayerStub();

        // Sub-layer render env is initialized upon creation
        Layer subLayer = layer.createSubLayer();
        Assert.assertEquals(renderEnv, subLayer.getRenderEnv());

        // Drawable render env is taken from parent layer, so is null until attached
        IImageDrawable image = CoreTestUtil.newImage();
        Assert.assertEquals(null, image.getRenderEnv());
        subLayer.add(image);
        Assert.assertEquals(renderEnv, image.getRenderEnv());

        // Recursively passes to sub-layers and sub-drawables within those layers
        RenderEnv newEnv = new RenderEnv(Dim.of(123, 456), Rect.EMPTY, Dim.EMPTY, false);
        layer.setRenderEnv(newEnv);
        Assert.assertEquals(newEnv, image.getRenderEnv());
    }

    private void assertRendered(Layer layer, IVisualElement... elems) {
        // Generate draw commands
        DrawBuffer buffer = new DrawBuffer();
        layer.draw(buffer);

        DrawBuffer layerBuffer = buffer.getLayerBuffer(0);
        List<? extends RenderCommand> cmd = layerBuffer.getCommands();

        Assert.assertEquals("Commands: " + cmd, elems.length, cmd.size());
        for (int n = 0; n < elems.length; n++) {
            Assert.assertEquals(elems[n].getZ(), ((QuadRenderCommand)cmd.get(n)).z);
        }
    }

    private static void assertSubLayers(Collection<? extends ILayer> expected, ILayer parentLayer) {
        Assert.assertEquals(expected, ImmutableList.copyOf(parentLayer.getSubLayers()));
    }

}
