package nl.weeaboo.vn.impl.test.integration;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.gdx.test.junit.GdxUiTest;

@Category(GdxUiTest.class)
public class TrivialIntegrationTest extends IntegrationTest {

    @Test
    public void render() {
        launcher.render();
    }

}
