package nl.weeaboo.vn.input;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public final class KeyCombinationTest {

    private final KeyCombination empty = combo();
    private final KeyCombination shl = combo(KeyCode.SHIFT_LEFT);
    private final KeyCombination shl2 = combo(KeyCode.SHIFT_LEFT, KeyCode.SHIFT_LEFT);
    private final KeyCombination shr = combo(KeyCode.SHIFT_RIGHT);

    /**
     * Duplicate keys passed to the constructor are removed.
     */
    @Test
    public void testDeduplication() {
        Assert.assertEquals(Arrays.asList(KeyCode.SHIFT_LEFT), new ArrayList<>(shl2.getKeys()));
    }

    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(empty)
            .addEqualityGroup(shl, combo(KeyCode.SHIFT_LEFT), shl2)
            .addEqualityGroup(shr)
            .testEquals();
    }

    private static KeyCombination combo(KeyCode... keys) {
        return new KeyCombination(Arrays.asList(keys));
    }

}
