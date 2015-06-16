package nl.weeaboo.entity;

import org.junit.Assert;
import org.junit.Test;

public class EntityAddRemoveTest {
	
	/**
	 * Checks that entities can be created/destroyed properly.
	 */
	@Test
	public void basicCreateDestroy() {
		World world = new World();
		Scene alpha = world.createScene();
		Scene beta = world.createScene();
		
		Entity entityA = alpha.createEntity();
		Assert.assertEquals(1, alpha.getEntitiesCount());
		Entity entityB = beta.createEntity();
		Assert.assertEquals(1, beta.getEntitiesCount());
		entityA.destroy();
		Assert.assertEquals(0, alpha.getEntitiesCount());
		Assert.assertEquals(1, beta.getEntitiesCount());
		entityB.destroy();
		Assert.assertEquals(0, beta.getEntitiesCount());
	}

	/**
	 * Tests the ability to move entities between scenes.
	 */
	@Test
	public void entityMove() {
		World world = new World();
		Scene alpha = world.createScene();
		Scene beta = world.createScene();
		
		Entity e = alpha.createEntity();
		Assert.assertEquals(e.getScene(), alpha);
		Assert.assertEquals(1, alpha.getEntitiesCount());
		Assert.assertEquals(0, beta.getEntitiesCount());
		e.moveToScene(beta);
		Assert.assertEquals(e.getScene(), beta);
		Assert.assertEquals(0, alpha.getEntitiesCount());
		Assert.assertEquals(1, beta.getEntitiesCount());
	}
	
}
