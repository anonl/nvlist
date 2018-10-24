package nl.weeaboo.vn.impl.test.integration.render.fx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.render.fx.BlurTask;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.test.integration.render.RenderIntegrationTest;

public class BlurTaskTest extends RenderIntegrationTest {

    private BlurTask blurTask;
    private ImageDrawable drawable;

    @Before
    public void before() {
        generate = true;

        drawable = new ImageDrawable();
        drawable.setSize(1280, 720);
    }

    @After
    public void after() {
        drawable.destroy();
    }

    @Test
    public void blur0() {
        blur(0.0);
        checkRenderResult("blurtask-000");
    }

    @Test
    public void blur10() {
        blur(10.0);
        checkRenderResult("blurtask-010");
    }

    @Test
    public void blur100() {
        blur(100.0);
        checkRenderResult("blurtask-100");
    }

    private void blur(double radius) {
        blurTask = new BlurTask(env.getImageModule(), getTexture("a"), radius);
        blurTask.render();

        drawable.setTexture(blurTask.getResult());
        drawable.draw(renderer.getDrawBuffer());
        renderer.render();
    }
}
