package nl.weeaboo.vn;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BaseEntityTest.class,
    ContextTest.class,
    DistortGridTest.class,
    ScreenshotTest.class,
    ScreenTest.class,
})
public class BaseTestSuite {

    static {
        TestUtil.configureLogger();
    }

}
