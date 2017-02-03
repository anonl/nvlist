package nl.weeaboo.vn.image;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.image.INinePatch.AreaId;

public class NinePatchTest {

    @Test
    public void testAreaEnum() {
        assertEnumAttribute(AreaId::isTop, AreaId.TOP_LEFT, AreaId.TOP, AreaId.TOP_RIGHT);
        assertEnumAttribute(AreaId::isBottom, AreaId.BOTTOM_LEFT, AreaId.BOTTOM, AreaId.BOTTOM_RIGHT);
        assertEnumAttribute(AreaId::isLeft, AreaId.TOP_LEFT, AreaId.LEFT, AreaId.BOTTOM_LEFT);
        assertEnumAttribute(AreaId::isRight, AreaId.TOP_RIGHT, AreaId.RIGHT, AreaId.BOTTOM_RIGHT);
    }

    private void assertEnumAttribute(Predicate<AreaId> predicate, AreaId... expected) {
        Set<AreaId> expectedSet = ImmutableSet.copyOf(expected);
        for (AreaId area : AreaId.values()) {
            Assert.assertEquals(expectedSet.contains(area), predicate.test(area));
        }
    }

}
