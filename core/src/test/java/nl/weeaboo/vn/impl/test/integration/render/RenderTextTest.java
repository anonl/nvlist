package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;
import nl.weeaboo.vn.text.ILoadingFontStore;

@Category(GdxUiTest.class)
public class RenderTextTest extends RenderIntegrationTest {

    @Test
    public void testRender() {
        ITextLayout layout = createLayout(styledText("---"), -1);

        drawText(0, 0, layout);
        render();

        checkRenderResult("text");
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
