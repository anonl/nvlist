package nl.weeaboo.vn.gdx.input;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Dim;
import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.GdxViewportUtil;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

public class GdxInputAdapterTest {

    private GdxInputAdapter adapter;

    @Before
    public void before() {
        HeadlessGdx.init();

        Viewport viewport = new FitViewport(640, 480);
        GdxViewportUtil.setToOrtho(viewport, Dim.of(640, 480), true);
        viewport.update(640, 480, false);

        adapter = new GdxInputAdapter(viewport);
    }

    @Test
    public void convertKeyboard() throws IllegalArgumentException, IllegalAccessException {
        Set<Integer> unmapped = Sets.newTreeSet();

        Map<String, Integer> keyConstants = ReflectUtil.getConstants(Keys.class, Integer.TYPE);
        for (Map.Entry<String, Integer> entry : keyConstants.entrySet()) {
            int keycode = entry.getValue();
            KeyCode result = GdxInputAdapter.convertKeyboard(keycode);
            if (result == KeyCode.UNKNOWN) {
                unmapped.add(keycode);
            }
        }

        Set<Integer> expectedUnsupported = ImmutableSet.of(
                Keys.ANY_KEY, // There is no 'any' key
                Keys.UNKNOWN, // Unknown should map to unknown
                Keys.META_SHIFT_RIGHT_ON, // These 'META' constants are not keys
                Keys.MAX_KEYCODE // Not a key
        );
        Assert.assertEquals(expectedUnsupported, unmapped);
    }

    @Test
    public void convertMouse() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Integer> keyConstants = ReflectUtil.getConstants(Buttons.class, Integer.TYPE);
        for (Map.Entry<String, Integer> entry : keyConstants.entrySet()) {
            int button = entry.getValue();
            KeyCode result = GdxInputAdapter.convertMouse(button);
            Assert.assertNotEquals(KeyCode.UNKNOWN, result);
        }

        Assert.assertEquals(KeyCode.UNKNOWN, GdxInputAdapter.convertMouse(12345));
    }

    @Test
    public void handleMouseEvents() throws IllegalArgumentException {
        final int pointer = 0;
        final int button = Buttons.LEFT;

        // Initially, nothing is pressed
        adapter.touchDown(1, 2, pointer, button);
        assertMousePos(1, 2);
        assertPressed(KeyCode.MOUSE_LEFT);

        adapter.touchDragged(3, 4, pointer);
        assertMousePos(3, 4);
        assertPressed(KeyCode.MOUSE_LEFT);

        // Release the mouse button
        adapter.touchUp(5, 6, pointer, button);
        assertMousePos(5, 6);
        assertPressed();

        adapter.mouseMoved(7, 8);
        assertMousePos(7, 8);

        adapter.scrolled(1, 2);
        CoreTestUtil.assertEquals(1, 2, getInput().getPointerScrollXY(), 0.0);
    }

    @Test
    public void handleKeyboardEvents() throws IllegalArgumentException {
        assertPressed();

        adapter.keyDown(Input.Keys.A);
        assertPressed(KeyCode.A);

        adapter.keyUp(Input.Keys.A);
        assertPressed();

        // 'keyTyped' events are ignored
        adapter.keyTyped('a');
        assertPressed();
    }

    private void assertMousePos(int x, int y) {
        INativeInput input = getInput();
        Vec2 pos = input.getPointerPos(Matrix.identityMatrix());
        CoreTestUtil.assertEquals(x, y, pos, 1e-3);
    }

    private void assertPressed(KeyCode... expected) {
        INativeInput input = getInput();

        Set<KeyCode> expectedSet = ImmutableSet.copyOf(expected);
        for (KeyCode key : KeyCode.values()) {
            Assert.assertEquals(expectedSet.contains(key), input.isPressed(key, false));
        }
    }

    private INativeInput getInput() {
        adapter.update();
        return adapter.getInput();
    }

}
