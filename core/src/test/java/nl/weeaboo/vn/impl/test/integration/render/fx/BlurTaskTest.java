package nl.weeaboo.vn.impl.test.integration.render.fx;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.fx.BlurTask;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.test.integration.render.RenderIntegrationTest;

@Category(GdxUiTest.class)
public class BlurTaskTest extends RenderIntegrationTest {

    private ImageDrawable drawable;

    @Before
    public void before() {
        drawable = new ImageDrawable();
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

    @Ignore("This test doesn't produce reproducible results across different machines")
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
        BlurTask blurTask = new BlurTask(getEnv().getImageModule(), getTexture("a"), radius);
        blurTask.render();

        ITexture tex = blurTask.getResult();
        setFilterNearest(tex);
        drawable.setTexture(tex);
        drawable.setBounds(0, 0, 1280, 720);

        drawable.draw(getDrawBuffer());
        render();
    }
}
