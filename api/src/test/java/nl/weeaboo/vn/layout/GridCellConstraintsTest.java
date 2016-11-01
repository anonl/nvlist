package nl.weeaboo.vn.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GridCellConstraintsTest {

    private GridCellConstraints constr;

    @Before
    public void before() {
        constr = new GridCellConstraints();
    }

    @Test
    public void growFunctions() {
        // Default constructor grows in neither direction
        assertGrow(false, false);

        // Grow X
        constr = new GridCellConstraints().growX();
        assertGrow(true, false);

        // Grow Y
        constr = new GridCellConstraints().growY();
        assertGrow(false, true);

        // Grow both
        constr = new GridCellConstraints().grow();
        assertGrow(true, true);
    }

    private void assertGrow(boolean expectGrowX, boolean expectGrowY) {
        Assert.assertEquals(expectGrowX, constr.growX);
        Assert.assertEquals(expectGrowY, constr.growY);
    }

}
