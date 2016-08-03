package nl.weeaboo.vn.layout.impl;

import org.junit.Before;
import org.junit.Test;

public class GridLayoutTest {

    private GridLayout layout;
    private LayoutTestHelper helper;

    @Before
    public void before() {
        layout = new GridLayout(new DummyLayoutGroupPeer());
        helper = new LayoutTestHelper(layout);
    }

    @Test
    public void singleRow() {
        DummyLayoutElem alpha = addDummy();
        DummyLayoutElem beta = addDummy();
        DummyLayoutElem gamma = addDummy();

        alpha.setLayoutWidths(0.0, 50.0, 100.0);
        beta.setLayoutWidths(20.0, 25.0, 200.0);
        gamma.setLayoutWidths(Double.NaN, Double.POSITIVE_INFINITY, 50.0);
        helper.layout(400, 300);

        // Because gamma has an infinitely large preferred width, it takes up all the remaining room after the
        // other elements have reached their preferred sizes.
        helper.assertSize(alpha, 50, 300);
        helper.assertSize(beta, 25, 300);
        helper.assertSize(gamma, 325, 300);

        // Change gamma so its preferred width is limited
        // All elements now reach their max widths
        gamma.setLayoutWidths(Double.NaN, 25.0, 50.0);
        helper.layout(400, 300);
        helper.assertSize(alpha, 100, 300);
        helper.assertSize(beta, 200, 300);
        helper.assertSize(gamma, 50, 300);
    }

    private DummyLayoutElem addDummy() {
        DummyLayoutElem elem = new DummyLayoutElem();
        layout.add(elem, new GridCellConstraints());
        return elem;
    }
}
