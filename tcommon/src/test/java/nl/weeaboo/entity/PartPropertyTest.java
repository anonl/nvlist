package nl.weeaboo.entity;

import org.junit.Assert;
import org.junit.Test;

public class PartPropertyTest {
	
	/**
	 * Tests an unsorted stream.
	 */
	@Test
	public void unsortedStream() {
		final TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		Scene scene = world.createScene();
		
		CountingPartListener pl = new CountingPartListener();
		scene.addPartListener(pl);
		
		Entity e = scene.createEntity();
		ModelPart part = new ModelPart(1, 2, 3);
		e.addPart(pr.typeA, part);
		
		Assert.assertEquals(0, pl.propertyChanges);
		part.setX(4);
		Assert.assertEquals(1, pl.propertyChanges);
		part.setX(5);
		Assert.assertEquals(2, pl.propertyChanges);
		part.setX(5);
		Assert.assertEquals(2, pl.propertyChanges);
	}
	
}
