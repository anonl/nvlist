package nl.weeaboo.vn.image;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.image.INinePatch.EArea;

public class NinePatchTest {

    @Test
    public void testAreaEnum() {
        assertEnumAttribute(EArea::isTop, EArea.TOP_LEFT, EArea.TOP, EArea.TOP_RIGHT);
        assertEnumAttribute(EArea::isBottom, EArea.BOTTOM_LEFT, EArea.BOTTOM, EArea.BOTTOM_RIGHT);
        assertEnumAttribute(EArea::isLeft, EArea.TOP_LEFT, EArea.LEFT, EArea.BOTTOM_LEFT);
        assertEnumAttribute(EArea::isRight, EArea.TOP_RIGHT, EArea.RIGHT, EArea.BOTTOM_RIGHT);
    }

    private void assertEnumAttribute(Predicate<EArea> predicate, EArea... expected) {
        Set<EArea> expectedSet = ImmutableSet.copyOf(expected);
        for (EArea area : EArea.values()) {
            Assert.assertEquals(expectedSet.contains(area), predicate.test(area));
        }
    }

}
