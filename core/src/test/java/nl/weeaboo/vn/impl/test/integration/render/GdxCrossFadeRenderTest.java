package nl.weeaboo.vn.impl.test.integration.render;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.gdx.graphics.GdxCrossFadeRenderer;
import nl.weeaboo.vn.impl.image.CrossFadeConfig;
import nl.weeaboo.vn.impl.scene.ImageDrawable;

public class GdxCrossFadeRenderTest extends RenderIntegrationTest {

    private CrossFadeConfig tweenConfig;
    private GdxCrossFadeRenderer tween;
    private ImageDrawable drawable;

    @Before
    public void before() {
        tweenConfig = new CrossFadeConfig(30);
        tweenConfig.setStartTexture(getTexture("a"));
        tweenConfig.setEndTexture(getTexture("b"));

        tween = new GdxCrossFadeRenderer(env.getImageModule(), tweenConfig);
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
        checkRenderResult("gdx-crossfade-000");
    }

    @Test
    public void render50() {
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-crossfade-050");
    }

    @Test
    public void render100() {
        setTime(1.0);
        doRender();
        checkRenderResult("gdx-crossfade-100");
    }

    @Test
    public void renderBlankStart() {
        tweenConfig.setStartTexture(null);
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-crossfade-blank-start");
    }

    @Test
    public void renderBlankEnd() {
        tweenConfig.setEndTexture(null);
        setTime(0.5);
        doRender();
        checkRenderResult("gdx-crossfade-blank-end");
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
