package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.impl.render.DrawBuffer;

@Category(GdxUiTest.class)
public class ScreenshotFunctionTest extends RenderIntegrationTest {

    @Test
    public void takeScreenshot() {
        loadScript("integration/screenshot/screenshotfunction");

        DrawBuffer drawBuffer = getDrawBuffer();

        novel.draw(drawBuffer);
        render();
        waitForAllThreads();

        drawBuffer.reset();
        novel.draw(drawBuffer);
        render();
        checkRenderResult("screenshot-function");
    }

}
