package nl.weeaboo.vn.impl.debug;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.DisposeUtil;
import nl.weeaboo.vn.impl.core.Context;
import nl.weeaboo.vn.impl.core.ContextManager;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.impl.input.InputMock;
import nl.weeaboo.vn.impl.text.TextRendererMock;
import nl.weeaboo.vn.input.VKey;

public final class OsdTest {

    private final TextRendererMock textRenderer = new TextRendererMock();
    private final PerformanceMetricsStub perfMetrics = new PerformanceMetricsStub();

    private TestEnvironment env;
    private SpriteBatch batch;
    private Osd osd;

    @Before
    public void before() {
        HeadlessGdx.init();
        env = TestEnvironment.newInstance();

        batch = new SpriteBatch();
        osd = new Osd(perfMetrics);
        osd.setVisible(true);
    }

    @After
    public void after() {
        DisposeUtil.dispose(batch);
    }

    @Test
    public void testBasicBehavior() {
        String resolutionAndMouse = "\nResolution: [0, 75, 800, 450]"
                + "\nMouse: (0, 0)";
        assertOsdText(perfMetrics.getPerformanceSummary() + resolutionAndMouse);

        // Activate some other stuff that shows up in the OSD text
        ContextManager contextManager = env.getContextManager();
        Context context = contextManager.createContext();
        contextManager.setContextActive(context, true);

        // Enable skip mode
        context.getSkipState().setSkipMode(SkipMode.PARAGRAPH);

        assertOsdText(perfMetrics.getPerformanceSummary()
                + "\nSkipping: PARAGRAPH"
                + "\n+ Layer(1280.0, 720.0): 0"
                + resolutionAndMouse);
    }

    /**
     * Visibility of the OSD can be toggled using F7 (if in debug mode).
     */
    @Test
    public void testVisibility() {
        osd.setVisible(false);

        // Entry debug mode and press the magic button
        env.getPrefStore().set(NovelPrefs.DEBUG, true);

        InputMock input = env.getInput();
        input.buttonPressed(VKey.TOGGLE_OSD);
        osd.update(env, input);

        // OSD became visible
        Assert.assertEquals(true, osd.isVisible());
    }

    private void assertOsdText(String expected) {
        osd.render(batch, env, textRenderer);
        Assert.assertEquals(expected, textRenderer.getText().toString());
    }

}
