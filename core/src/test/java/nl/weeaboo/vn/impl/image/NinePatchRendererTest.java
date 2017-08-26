package nl.weeaboo.vn.impl.image;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Insets2D;
import nl.weeaboo.test.InsetsAssert;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.INinePatch.AreaId;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.DrawBuffer;
import nl.weeaboo.vn.impl.render.QuadRenderCommand;
import nl.weeaboo.vn.impl.render.RenderCommand;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.test.CoreTestUtil;

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

        Collection<? extends RenderCommand> commands = drawBuffer.getCommands();
        Assert.assertEquals(0, commands.size());
    }

    @Test
    public void basicRender() {
        for (int n = 0; n < AreaId.values().length; n++) {
            renderer.setTexture(AreaId.values()[n], textures[n]);
        }
        ITexture center = textures[AreaId.CENTER.ordinal()];
        assertNativeSize(renderer, center.getWidth(), center.getHeight());

        // Cols = 1, 7, 2
        // Rows = 1, 6, 3
        renderer.setInsets(Insets2D.of(1, 2, 3, 1));
        InsetsAssert.assertEquals(Insets2D.of(1, 2, 3, 1), renderer.getInsets(), EPSILON);
        assertNativeSize(renderer, center.getWidth() + 1 + 2, center.getHeight() + 1 + 3);

        renderer.render(drawBuffer, imageDrawable, Area2D.of(0, 0, 10, 10));

        Collection<? extends RenderCommand> commands = drawBuffer.getCommands();
        Assert.assertEquals(9, commands.size());

        // Check bounds of rendered segments
        double topY = 0;
        assertRenderBounds(AreaId.TOP_LEFT, Area2D.of(0, topY, 1, 1));
        assertRenderBounds(AreaId.TOP, Area2D.of(1, topY, 7, 1));
        assertRenderBounds(AreaId.TOP_RIGHT, Area2D.of(8, topY, 2, 1));
        double centerY = 1;
        assertRenderBounds(AreaId.LEFT, Area2D.of(0, centerY, 1, 6));
        assertRenderBounds(AreaId.CENTER, Area2D.of(1, centerY, 7, 6));
        assertRenderBounds(AreaId.RIGHT, Area2D.of(8, centerY, 2, 6));
        double bottomY = 7;
        assertRenderBounds(AreaId.BOTTOM_LEFT, Area2D.of(0, bottomY, 1, 3));
        assertRenderBounds(AreaId.BOTTOM, Area2D.of(1, bottomY, 7, 3));
        assertRenderBounds(AreaId.BOTTOM_RIGHT, Area2D.of(8, bottomY, 2, 3));
    }

    private void assertNativeSize(INinePatch ninePatch, double expectedW, double expectedH) {
        Assert.assertEquals(expectedW, ninePatch.getNativeWidth(), EPSILON);
        Assert.assertEquals(expectedH, ninePatch.getNativeHeight(), EPSILON);
    }

    private void assertRenderBounds(AreaId area, Area2D expected) {
        QuadRenderCommand command = findCommand(drawBuffer.getCommands(), area);
        RectAssert.assertEquals(expected, command.bounds, EPSILON);
    }

    private @Nullable QuadRenderCommand findCommand(List<? extends RenderCommand> commands, AreaId area) {
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
