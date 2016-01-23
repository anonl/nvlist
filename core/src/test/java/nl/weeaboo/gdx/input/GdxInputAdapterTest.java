package nl.weeaboo.gdx.input;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import nl.weeaboo.vn.core.KeyCode;

public class GdxInputAdapterTest {

    @Test
    public void convertKeyboard() throws IllegalArgumentException, IllegalAccessException {
        Set<Integer> unmapped = Sets.newHashSet();
        for (Field field : Keys.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
                Integer keycode = (Integer)field.get(null);
                KeyCode result = GdxInputAdapter.convertKeyboard(keycode);
                if (result == KeyCode.UNKNOWN) {
                    unmapped.add(keycode);
                }
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
        for (Field field : Buttons.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
                Integer button = (Integer)field.get(null);
                KeyCode result = GdxInputAdapter.convertMouse(button);
                if (result == KeyCode.UNKNOWN) {
                    unmapped.add(button);
                }
            }
        }

        Set<Integer> expectedUnsupported = ImmutableSet.of();
        Assert.assertEquals(expectedUnsupported, unmapped);
    }

}
