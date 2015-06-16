package nl.weeaboo.vn;

import nl.weeaboo.vn.math.MathTestSuite;
import nl.weeaboo.vn.script.ScriptTestSuite;
import nl.weeaboo.vn.script.lvn.LvnTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BaseTestSuite.class,
    MathTestSuite.class,
    ScriptTestSuite.class,
    LvnTestSuite.class,
})
public class AllUnitTests {

	static {
		TestUtil.configureLogger();
	}

}
