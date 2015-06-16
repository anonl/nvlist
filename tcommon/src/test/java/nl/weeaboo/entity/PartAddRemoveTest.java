package nl.weeaboo.entity;

import org.junit.Assert;
import org.junit.Test;

public class PartAddRemoveTest {
	
	/**
	 * Checks that parts can be added/removed from entities.
	 */
	@Test
	public void singleAddRemove() {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		
		Scene scene = world.createScene();
		CountingEntityListener el = new CountingEntityListener();
		scene.addEntityListener(el);
		CountingPartListener pl = new CountingPartListener();
		scene.addPartListener(pl);
		
		// Create entity
		Entity e = scene.createEntity();
		e.addPart(pr.typeA, new ModelPart());
		Assert.assertEquals(1, e.getPartsCount());
		e.addPart(pr.typeB, new ModelPart());
		Assert.assertEquals(2, e.getPartsCount());
		e.addPart(pr.typeC, new ModelPart());
		Assert.assertEquals(3, e.getPartsCount());
		
		// Check that events are correctly reported to listeners
		Assert.assertEquals(1, el.created);
		Assert.assertEquals(1, el.attached);
		Assert.assertEquals(3, pl.attached);

		// Destroy entity
		e.destroy();
		
		// Check that events are correctly reported to listeners		
		Assert.assertEquals(3, pl.detached); // Destroying the entity should detach its parts
		Assert.assertEquals(1, el.detached);
		Assert.assertEquals(1, el.destroyed);
	}
	
	/**
	 * A more complex test for add/remove behavior.
	 */
	@Test
	public void multiAddRemove() {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		
		Scene scene = world.createScene();
		CountingEntityListener el = new CountingEntityListener();
		scene.addEntityListener(el);
		CountingPartListener pl = new CountingPartListener();
		scene.addPartListener(pl);
		
		// Create entities
		int count = 10;
		Entity[] entities = new Entity[count];
		for (int n = 0; n < count; n++) {
			Entity e = scene.createEntity();
			e.addPart(pr.typeA, new ModelPart());
			e.addPart(pr.typeB, new ModelPart());
			e.addPart(pr.typeC, new ModelPart());
			entities[n] = e;
		}
		
		// Destroy entities
		for (Entity e : entities) {
			e.destroy();
		}
		
		// Check listener counts
		Assert.assertEquals(count, el.created);
		Assert.assertEquals(count, el.attached);
		Assert.assertEquals(3 * count, pl.attached);
		Assert.assertEquals(3 * count, pl.detached);
		Assert.assertEquals(count, el.detached);
		Assert.assertEquals(count, el.destroyed);
	}
	
	/**
	 * A more complex test for add/remove behavior.
	 */
	@Test
	public void partGetSet() {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		
		Scene scene = world.createScene();
		Entity e = scene.createEntity();
		
		ModelPart alpha = new ModelPart();
		ModelPart beta = new ModelPart();
		
		// Double-add part
		e.addPart(pr.typeA, alpha);
		try {
			e.addPart(pr.typeA, alpha); // Double-add same part
			Assert.fail();
		} catch (IllegalArgumentException iae) {
			// Behavior ok, parts can't be overwritten with add
		}
		try {
			e.addPart(pr.typeA, beta); // Double-add different part
			Assert.fail();
		} catch (IllegalArgumentException iae) {
			// Behavior ok, parts can't be overwritten with add
		}
		
		// Double-remove part
		e.removePart(pr.typeA);
		e.removePart(pr.typeA);
		
		// Double-add part using setPart (overwrites)
		e.setPart(pr.typeA, alpha);		
		e.setPart(pr.typeA, beta);
		Assert.assertEquals(0, alpha.refcount);
		Assert.assertEquals(1, beta.refcount);
		
		// Remove part using setPart
		e.setPart(pr.typeA, null);
		Assert.assertEquals(0, alpha.refcount);
		Assert.assertEquals(0, beta.refcount);
		
		// Use same part for multiple types
		e.setPart(pr.typeA, alpha);
		e.setPart(pr.typeB, alpha);
		e.setPart(pr.typeC, alpha);
		Assert.assertEquals(3, alpha.refcount);
		
		// Get invalid part types
		Assert.assertNull(e.getPart(-1));
		Assert.assertNull(e.getPart(4));
		
		// Remove all
		e.removeAllParts();
		Assert.assertEquals(0, alpha.refcount);
		Assert.assertEquals(0, beta.refcount);
		Assert.assertEquals(0, e.getPartsCount());
	}
	
}
