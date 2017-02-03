package nl.weeaboo.vn.gdx.input;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.vn.input.KeyCode;

public class GdxInputAdapterTest {

    @Test
    public void convertKeyboard() throws IllegalArgumentException, IllegalAccessException {
        Set<Integer> unmapped = Sets.newHashSet();

        Map<String, Integer> keyConstants = ReflectUtil.getConstants(Keys.class, Integer.TYPE);
        for (Entry<String, Integer> entry : keyConstants.entrySet()) {
            int keycode = entry.getValue();
            KeyCode result = GdxInputAdapter.convertKeyboard(keycode);
            if (result == KeyCode.UNKNOWN) {
                unmapped.add(keycode);
            }
        }

        Set<Integer> expectedUnsupported = ImmutableSet.of(
                Keys.UNKNOWN, // Unknown should map to unknown
                Keys.ANY_KEY, // There is no 'any' key
                Keys.META_SHIFT_RIGHT_ON // These 'META' constants are not keys
        );
        Assert.assertEquals(expectedUnsupported, unmapped);
    }

    @Test
    public void convertMouse() throws IllegalArgumentException, IllegalAccessException {
        Set<Integer> unmapped = Sets.newHashSet();
        Map<String, Integer> keyConstants = ReflectUtil.getConstants(Buttons.class, Integer.TYPE);
        for (Entry<String, Integer> entry : keyConstants.entrySet()) {
            int button = entry.getValue();
            KeyCode result = GdxInputAdapter.convertMouse(button);
            if (result == KeyCode.UNKNOWN) {
                unmapped.add(button);
            }
        }

        Set<Integer> expectedUnsupported = ImmutableSet.of();
        Assert.assertEquals(expectedUnsupported, unmapped);
    }

}
