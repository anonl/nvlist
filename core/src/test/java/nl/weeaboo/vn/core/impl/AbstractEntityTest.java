package nl.weeaboo.vn.core.impl;

import org.junit.Before;

import nl.weeaboo.entity.Scene;
import nl.weeaboo.entity.World;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;

public class AbstractEntityTest {

	protected BasicPartRegistry pr;
	protected World world;
	protected Scene scene;

	@Before
	public void init() {
		pr = new BasicPartRegistry();
		world = new World(pr);
		scene = world.createScene();
	}

}
