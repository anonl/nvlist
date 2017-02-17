package nl.weeaboo.vn.impl.layout;

import org.junit.Before;
import org.junit.Test;

public class GridLayoutTest {

    private GridLayout layout;
    private LayoutTestHelper helper;

    @Before
    public void before() {
        layout = new GridLayout(new DummyLayoutElemPeer());
        helper = new LayoutTestHelper(layout);
    }

    @Test
    public void singleRow() {
        DummyLayoutElem alpha = new DummyLayoutElem();
        layout.add(alpha).grow();

        DummyLayoutElem beta = new DummyLayoutElem();
        layout.add(beta).grow();

        DummyLayoutElem gamma = new DummyLayoutElem();
        layout.add(gamma).grow();

        alpha.setLayoutWidths(0.0, 50.0, 100.0);
        beta.setLayoutWidths(20.0, 25.0, 200.0);
        gamma.setLayoutWidths(Double.NaN, Double.POSITIVE_INFINITY, Double.NaN);
        helper.layout(400, 300);

        // Because gamma has an infinitely large preferred width, it takes up all the remaining room after the
        // other elements have reached their preferred sizes.
        helper.assertSize(alpha, 100, 300);
        helper.assertSize(beta, 200, 300);
        helper.assertSize(gamma, 100, 300);

        // Change gamma so its preferred width is limited
        // All elements now reach their max widths
        gamma.setLayoutWidths(Double.NaN, 25.0, 50.0);
        helper.layout(400, 300);
        helper.assertSize(alpha, 100, 300);
        helper.assertSize(beta, 200, 300);
        helper.assertSize(gamma, 50, 300);
    }

    @Test
    public void growConstraint() {
        /*
         * A | B
         * -----
         * C | D
         */
        final DummyLayoutElem alpha = new DummyLayoutElem();
        layout.add(alpha).growX();

        final DummyLayoutElem beta = new DummyLayoutElem();
        layout.add(beta).growY();
        layout.endRow();

        final DummyLayoutElem gamma = new DummyLayoutElem();
        layout.add(gamma).growY();

        final DummyLayoutElem delta = new DummyLayoutElem();
        layout.add(delta).growX();

        alpha.setLayoutWidths(0.0, 50.0, 100.0);
        beta.setLayoutWidths(20.0, 25.0, 200.0);
        beta.setLayoutHeights(20.0, 30.0, 40.0);
        gamma.setLayoutWidths(0.0, 125.0, 200.0);
        gamma.setLayoutHeights(20.0, 25.0, 35.0);
        delta.setLayoutWidths(0.0, 10.0, 300.0);
        helper.layout(400, 300);

        helper.assertSize(alpha, 100, 10); // grow x (10 is the pref. height for alpha)
        helper.assertSize(beta, 25, 40);   // grow y (40 is the max. height for beta)
        helper.assertSize(gamma, 125, 35); // grow y (35 is the max. height for gamma)
        helper.assertSize(delta, 275, 10);  // grow x
    }

}
