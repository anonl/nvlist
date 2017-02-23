package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.gdx.graphics.GdxBitmapTweenRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.impl.scene.ImageDrawable;

public class GdxBitmapTweenRenderTest extends RenderIntegrationTest {

    private BitmapTweenConfig tweenConfig;
    private GdxBitmapTweenRenderer tween;
    private ImageDrawable drawable;

    @Before
    public void before() {
        ITexture control = getTexture("vshutter");
        tweenConfig = new BitmapTweenConfig(30, new ControlImage(control, false));
        tweenConfig.setStartTexture(getTexture("a"));
        tweenConfig.setEndTexture(getTexture("b"));

        tween = new GdxBitmapTweenRenderer(env.getImageModule(), tweenConfig);
        drawable = new ImageDrawable();
        drawable.setRenderer(tween);
        drawable.setSize(1280, 720);
    }

    @After
    public void after() {
        drawable.destroy();
    }

    @Test
    public void renderStart() {
        setTime(0.0);
        doRender();
        checkRenderResult("gdx-bitmaptween-000");
    }

    @Test
    public void render50() {
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-bitmaptween-050");
    }

    @Test
    public void render100() {
        setTime(1.0);
        doRender();
        checkRenderResult("gdx-bitmaptween-100");
    }

    @Test
    public void renderBlankStart() {
        tweenConfig.setStartTexture(null);
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-bitmaptween-blank-start");
    }

    @Test
    public void renderBlankEnd() {
        tweenConfig.setEndTexture(null);
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-bitmaptween-blank-end");
    }

    private void setTime(double relativeTime) {
        tween.setTime(Math.floor(relativeTime * tween.getDuration()) - 1);
        tween.update();
    }

    private void doRender() {
        drawable.draw(renderer.getDrawBuffer());
        renderer.render();
    }
}
