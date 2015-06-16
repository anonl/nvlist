package nl.weeaboo.vn.script;

import nl.weeaboo.vn.script.lua.BaseScriptTest;
import nl.weeaboo.vn.script.lua.lib.CoreLibTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BaseScriptTest.class,
    CoreLibTest.class
})
public class ScriptTestSuite {

}
