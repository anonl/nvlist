package nl.weeaboo.vn.signal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.vn.core.RenderEnvStub;

public class RenderEnvChangeSignalTest {

    private ExceptionTester exTester;

    @Before
    public void before() {
        exTester = new ExceptionTester();
    }

    /** Element param must be non-null */
    @Test
    public void nullElem() {
        exTester.expect(IllegalArgumentException.class, () -> {
            return new RenderEnvChangeSignal(null);
        });
    }

    @Test
    public void validElem() {
        RenderEnvStub renderEnv = new RenderEnvStub();

        RenderEnvChangeSignal signal = new RenderEnvChangeSignal(renderEnv);
        Assert.assertSame(renderEnv, signal.getRenderEnv());

        signal.setHandled();
        Assert.assertSame(renderEnv, signal.getRenderEnv());
    }

}
