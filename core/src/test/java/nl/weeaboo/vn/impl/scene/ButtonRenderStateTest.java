package nl.weeaboo.vn.impl.scene;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.scene.Button;
import nl.weeaboo.vn.impl.scene.ButtonModel;
import nl.weeaboo.vn.impl.scene.ButtonRenderer;
import nl.weeaboo.vn.impl.script.ScriptEventDispatcher;
import nl.weeaboo.vn.impl.text.TestFontStore;
import nl.weeaboo.vn.scene.ButtonViewState;

/** Test passing of information between {@link Button} and {@link ButtonRenderer} */
public class ButtonRenderStateTest {

    private ButtonModel model;
    private ButtonRenderer renderer;
    private Button button;

    @Before
    public void before() {
        model = new ButtonModel();
        renderer = new ButtonRenderer();

        StaticEnvironment.FONT_STORE.set(new TestFontStore());

        button = new Button(new ScriptEventDispatcher(), model, renderer);
        button.setSize(100, 20);
    }

    @After
    public void after() {
        button.destroy();

        StaticEnvironment.FONT_STORE.set(null);
    }

    @Test
    public void viewState() {
        // Initial state
        checkViewState(ButtonViewState.DEFAULT);

        // Rollover (hover)
        model.setRollover(true);
        checkViewState(ButtonViewState.ROLLOVER);

        // Pressed overrides hover
        model.setPressed(true);
        checkViewState(ButtonViewState.PRESSED);

        // Disabled overrides pressed
        model.setEnabled(false);
        checkViewState(ButtonViewState.DISABLED);
    }

    /** The vertical align methods on button delegate to the renderer */
    @Test
    public void delegateVerticalAlign() {
        for (VerticalAlign valign : VerticalAlign.values()) {
            // Set valign through button
            button.setVerticalAlign(valign);
            Assert.assertEquals(valign, button.getVerticalAlign());
            Assert.assertEquals(valign, renderer.getVerticalAlign());

            // Set valign through renderer
            renderer.setVerticalAlign(valign);
            Assert.assertEquals(valign, button.getVerticalAlign());
            Assert.assertEquals(valign, renderer.getVerticalAlign());
        }
    }


    /** get/set text methods on button delegate to the renderer */
    @Test
    public void delegateSetText() {
        button.setText("abc");
        Assert.assertEquals("abc", button.getText().toString());
    }

    private void checkViewState(ButtonViewState expected) {
        button.onTick();
        Assert.assertEquals(expected, renderer.getViewState());
    }

}
