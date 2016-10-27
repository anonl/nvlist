package nl.weeaboo.vn.test.integration.render;

import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.graphics.GdxBitmapTweenRenderer;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig;
import nl.weeaboo.vn.image.impl.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.scene.impl.ImageDrawable;

public class GdxBitmapTweenRenderTest extends RenderIntegrationTest {

    private GdxBitmapTweenRenderer tween;
    private ImageDrawable drawable;

    @Before
    public void before() {
        ITexture control = getTexture("vshutter");
        BitmapTweenConfig tweenConfig = new BitmapTweenConfig(30, new ControlImage(control, false));
        tweenConfig.setStartTexture(getTexture("a"));
        tweenConfig.setEndTexture(getTexture("b"));

        tween = new GdxBitmapTweenRenderer(env.getImageModule(), tweenConfig);
        drawable = new ImageDrawable();
        drawable.setRenderer(tween);
        drawable.setSize(1280, 720);
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

    private void setTime(double relativeTime) {
        tween.setTime(Math.floor(relativeTime * tween.getDuration()) - 1);
        tween.update();
    }

    private void doRender() {
        renderer.startLayer();
        drawable.draw(renderer.getDrawBuffer());
        renderer.render();
    }
}
