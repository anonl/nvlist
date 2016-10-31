package nl.weeaboo.vn.test.integration.render;

import org.junit.Test;

import nl.weeaboo.vn.render.impl.DrawBuffer;

public class ScreenshotFunctionTest extends RenderIntegrationTest {

    @Test
    public void takeScreenshot() {
        loadScript("integration/screenshot/screenshotfunction");

        DrawBuffer drawBuffer = getDrawBuffer();

        novel.draw(drawBuffer);
        renderer.render();
        waitForAllThreads();

        drawBuffer.reset();
        novel.draw(drawBuffer);
        renderer.render();
        checkRenderResult("screenshot-function");
    }

}
