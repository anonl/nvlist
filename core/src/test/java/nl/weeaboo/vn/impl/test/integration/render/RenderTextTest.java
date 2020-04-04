package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;
import nl.weeaboo.vn.impl.render.DrawTransform;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.text.ILoadingFontStore;

@Category(GdxUiTest.class)
public class RenderTextTest extends RenderIntegrationTest {

    private DrawTransform transform;

    @Before
    public void before() {
        // Render tests by default run at 50% scale, use a transform to render at 100% scale
        transform = new DrawTransform();
        transform.setTransform(Matrix.scaleMatrix(2, 2));
    }

    @Test
    public void testRender() {
        ITextLayout layout = createLayout(styledText("---"), -1);

        drawText(transform, 0, 0, layout);
        render();

        checkRenderResult("text");
    }

    /**
     * Text is rendered at integer coordinates to improve sharpness.
     */
    @Test
    public void testSnapToGrid() {
        ITextLayout layout = createLayout(styledText("---"), -1);

        drawText(transform, 0, 0.5, layout);
        render();

        checkRenderResult("text-snap-to-grid");
    }

    private StyledText styledText(String string) {
        return new StyledText(string, new TextStyle("RobotoSlab", 16));
    }

    private ITextLayout createLayout(StyledText text, int wrapWidth) {
        LayoutParameters layoutParams = new LayoutParameters();
        layoutParams.ydir = 1;
        layoutParams.wrapWidth = wrapWidth;

        ILoadingFontStore fontStore = env.getTextModule().getFontStore();
        return LayoutUtil.layout(fontStore, text, layoutParams);
    }

}
