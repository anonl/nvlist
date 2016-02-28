package nl.weeaboo.vn.image.impl;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.EArea;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.render.impl.QuadRenderCommand;
import nl.weeaboo.vn.render.impl.RenderCommand;
import nl.weeaboo.vn.scene.impl.ImageDrawable;

public class NinePatchRendererTest {

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private NinePatchRenderer renderer;
    private DrawBuffer drawBuffer;
    private ImageDrawable imageDrawable;
    private ITexture[] textures;

    @Before
    public void before() {
        drawBuffer = new DrawBuffer();
        renderer = new NinePatchRenderer();
        imageDrawable = new ImageDrawable();

        textures = new ITexture[9];
        for (int n = 0; n < textures.length; n++) {
            textures[n] = new TestTexture();
        }
    }

    /** Test rendering when no areas have textures */
    @Test
    public void emptyRender() {
        assertNativeSize(renderer, 0, 0);
        renderer.render(drawBuffer, imageDrawable, Area2D.of(0, 0, 10, 10));

        Collection<RenderCommand> commands = drawBuffer.getCommands();
        Assert.assertEquals(0, commands.size());
    }

    @Test
    public void basicRender() {
        for (int n = 0; n < EArea.values().length; n++) {
            renderer.setTexture(EArea.values()[n], textures[n]);
        }
        assertNativeSize(renderer, 0, 0);

        // Cols = 1, 7, 2
        // Rows = 1, 6, 3
        renderer.setInsets(Insets2D.of(1, 2, 3, 1));
        CoreTestUtil.assertEquals(Insets2D.of(1, 2, 3, 1), renderer.getInsets());
        assertNativeSize(renderer, 1 + 2, 1 + 3);

        renderer.render(drawBuffer, imageDrawable, Area2D.of(0, 0, 10, 10));

        Collection<RenderCommand> commands = drawBuffer.getCommands();
        Assert.assertEquals(9, commands.size());

        // Check bounds of rendered segments
        double bottomY = 0;
        assertRenderBounds(EArea.BOTTOM_LEFT, Area2D.of(0, bottomY, 1, 3));
        assertRenderBounds(EArea.BOTTOM, Area2D.of(1, bottomY, 7, 3));
        assertRenderBounds(EArea.BOTTOM_RIGHT, Area2D.of(8, bottomY, 2, 3));
        double centerY = 3;
        assertRenderBounds(EArea.LEFT, Area2D.of(0, centerY, 1, 6));
        assertRenderBounds(EArea.CENTER, Area2D.of(1, centerY, 7, 6));
        assertRenderBounds(EArea.RIGHT, Area2D.of(8, centerY, 2, 6));
        double topY = 9;
        assertRenderBounds(EArea.TOP_LEFT, Area2D.of(0, topY, 1, 1));
        assertRenderBounds(EArea.TOP, Area2D.of(1, topY, 7, 1));
        assertRenderBounds(EArea.TOP_RIGHT, Area2D.of(8, topY, 2, 1));
    }

    private void assertNativeSize(INinePatch ninePatch, double expectedW, double expectedH) {
        Assert.assertEquals(expectedW, ninePatch.getNativeWidth(), EPSILON);
        Assert.assertEquals(expectedH, ninePatch.getNativeHeight(), EPSILON);
    }

    private void assertRenderBounds(EArea area, Area2D expected) {
        QuadRenderCommand command = findCommand(drawBuffer.getCommands(), area);
        CoreTestUtil.assertEquals(expected, command.bounds);
    }

    private QuadRenderCommand findCommand(ImmutableList<RenderCommand> commands, EArea area) {
        ITexture texture = renderer.getTexture(area);
        for (RenderCommand command : commands) {
            if (command instanceof QuadRenderCommand) {
                QuadRenderCommand qrc = (QuadRenderCommand)command;
                if (qrc.tex == texture) {
                    return qrc;
                }
            }
        }
        return null;
    }

}
