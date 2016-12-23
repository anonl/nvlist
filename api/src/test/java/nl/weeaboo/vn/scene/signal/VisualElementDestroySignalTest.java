package nl.weeaboo.vn.scene.signal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.vn.scene.VisualElementStub;

public class VisualElementDestroySignalTest {

    private ExceptionTester exTester;

    @Before
    public void before() {
        exTester = new ExceptionTester();
    }

    /** Element param must be non-null */
    @Test
    public void nullElem() {
        exTester.expect(NullPointerException.class, () -> {
            return new VisualElementDestroySignal(null);
        });
    }

    @Test
    public void validElem() {
        VisualElementStub elem = new VisualElementStub();

        VisualElementDestroySignal signal = new VisualElementDestroySignal(elem);
        Assert.assertSame(elem, signal.getDestroyedElement());

        signal.setHandled();
        Assert.assertSame(elem, signal.getDestroyedElement());
    }

}
