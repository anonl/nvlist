package nl.weeaboo.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SceneAddRemoveTest {

	/**
	 * Adds and then removes scenes.
	 */
	@Test
	public void simpleCreateDestroy() {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);

		// Create some scenes with some entities
		List<Scene> scenes = new ArrayList<Scene>();
		for (int s = 0; s < 3; s++) {
			Scene scene = world.createScene();
			scenes.add(scene);
			for (int e = 0; e < 3; e++) {
                scene.createEntity();
			}
		}

		// Destroy the scenes in some order
		destroyScene(scenes.get(1));
		destroyScene(scenes.get(0));
		destroyScene(scenes.get(2));
	}

	private static void destroyScene(Scene s) {
		List<Entity> entities = s.getEntities();
		int entitiesCount = entities.size();

		Assert.assertEquals(false, s.isDestroyed());
		for (Entity e : entities) {
			Assert.assertEquals(false, e.isDestroyed());
		}

		s.destroy();

		Assert.assertEquals(true, s.isDestroyed());
		Assert.assertEquals(0, s.getEntitiesCount());
		// Make sure entities list is a copy, and not a reference.
		Assert.assertEquals(entitiesCount, entities.size());
		for (Entity e : entities) {
			Assert.assertEquals(true, e.isDestroyed());
		}
	}

}
