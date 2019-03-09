package nl.weeaboo.vn.impl.test.integration.render.fx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.render.fx.ImageCompositeConfig;
import nl.weeaboo.vn.impl.render.fx.ImageCompositeConfig.TextureEntry;
import nl.weeaboo.vn.impl.render.fx.ImageCompositeTask;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.test.integration.render.RenderIntegrationTest;

@Category(GdxUiTest.class)
public class ImageCompositeTaskTest extends RenderIntegrationTest {

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
    public void testComposite() {
        ImageCompositeConfig config = new ImageCompositeConfig();

        TextureEntry a = new TextureEntry(getTexture("a"));
        config.add(a);

        TextureEntry b = new TextureEntry(getTexture("b"));
        b.setBlendMode(BlendMode.ADD);
        b.setPos(320, 180);
        config.add(b);

        config.setSize(640, 360);

        doRender(config);

        checkRenderResult("composite-000");
    }

    private void doRender(ImageCompositeConfig config) {
        ImageCompositeTask task = new ImageCompositeTask(env.getImageModule(), config);
        task.render();

        ITexture tex = task.getResult();
        setFilterNearest(tex);
        drawable.setTexture(tex);
        drawable.setBounds(0, 0, 1280, 720);

        drawable.draw(getDrawBuffer());
        render();
    }
}
