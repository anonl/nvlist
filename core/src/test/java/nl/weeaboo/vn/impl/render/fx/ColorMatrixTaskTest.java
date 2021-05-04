package nl.weeaboo.vn.impl.render.fx;

import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.scene.ImageDrawable;
import nl.weeaboo.vn.impl.test.integration.render.RenderIntegrationTest;

@Category(GdxUiTest.class)
public class ColorMatrixTaskTest extends RenderIntegrationTest {

    private ImageDrawable drawable;

    @Before
    public void before() {
        drawable = new ImageDrawable();
    }

    @Test
    public void testInvert() {
        ColorMatrix m = new ColorMatrix();
        m.setOffsets(1, 1, 1, 0);
        m.setDiagonals(-1, -1, -1, 1);
        colorMatrix(m);

        checkRenderResult("colormatrix-invert");
    }

    private void colorMatrix(ColorMatrix colorMatrix) {
        colorMatrix(colorMatrix, getTexture("a"));
    }

    private void colorMatrix(ColorMatrix colorMatrix, @Nullable ITexture texture) {
        ColorMatrixTask task = new ColorMatrixTask(getEnv().getImageModule(), texture,
                colorMatrix);
        task.render();

        ITexture tex = task.getResult();
        setFilterNearest(tex);
        drawable.setTexture(tex);
        drawable.setBounds(0, 0, 1280, 720);
        drawable.draw(getDrawBuffer());
        render();
    }

}
