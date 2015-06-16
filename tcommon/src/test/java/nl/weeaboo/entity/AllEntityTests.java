package nl.weeaboo.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BasicUsageTest.class,
	SceneAddRemoveTest.class,
	EntityAddRemoveTest.class,
	PartAddRemoveTest.class,
	PartPropertyTest.class,
	SaveLoadTest.class})
public class AllEntityTests {

}
