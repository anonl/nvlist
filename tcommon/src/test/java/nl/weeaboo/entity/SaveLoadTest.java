package nl.weeaboo.entity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.common.StringUtil;

public class SaveLoadTest {

	private static final File TEMP_FILE = new File("test.bin");

	@Before
	public void init() {
		for (Handler h : EntityLog.getInstance().getParent().getHandlers()) {
			h.setLevel(Level.CONFIG);
		}
	}

	/**
	 * Tests serialization of Scene objects.
	 */
	@Test
	public void writeScene() throws IOException, ClassNotFoundException {
		World world = new World();
		Scene scene = world.createScene();

		//Create some entities to save
		for (int n = 0; n < 3; n++) {
            scene.createEntity();
		}

		//Save the scene
		TestUtil.serializeWorld(TEMP_FILE, false, world);

		//Load the scene
		World loaded = TestUtil.deserializeWorld(TEMP_FILE);

		//Validate
		TestUtil.assertEntitiesEqual(scene.getEntities(), loaded.getScene(scene.getId()).getEntities());
	}

	/**
	 * Tests serialization of Entities with Parts.
	 */
	@Test
	public void writeParts() throws IOException, ClassNotFoundException {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);

		Scene scene = world.createScene();
		Entity e = scene.createEntity();

		ModelPart alpha = new ModelPart();
		ModelPart beta = new ModelPart();
		e.addPart(pr.typeA, alpha);
		e.addPart(pr.typeB, alpha);
		e.addPart(pr.typeC, beta);

		TestUtil.serializeWorld(TEMP_FILE, false, world);
		Assert.assertEquals(1, scene.getEntitiesCount());

		World loaded = TestUtil.deserializeWorld(TEMP_FILE);
		Assert.assertEquals(1, scene.getEntitiesCount());

		TestUtil.assertEntitiesEqual(e, loaded.findEntity(e.getId()));
		Assert.assertEquals(2, e.getPart(pr.typeA).refcount);
		Assert.assertEquals(2, e.getPart(pr.typeB).refcount);
		Assert.assertTrue(e.getPart(pr.typeA) == e.getPart(pr.typeB));
		Assert.assertEquals(1, e.getPart(pr.typeC).refcount);
	}


	/**
	 * Tests serialization of Entities with Parts.
	 */
	@Test
	public void writeEfficiency() throws IOException, ClassNotFoundException {
		TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);

		long t0 = System.nanoTime();

		int sceneCount = 100;
		int entityCount = 1000;
		for (int s = 0; s < sceneCount; s++) {
			Scene scene = world.createScene();
			for (int e = 0; e < entityCount; e++) {
				Entity entity = scene.createEntity();

				ModelPart alpha = new ModelPart();
				ModelPart beta = new ModelPart();
				entity.addPart(pr.typeA, alpha);
				entity.addPart(pr.typeB, alpha);
				entity.addPart(pr.typeC, beta);
			}
		}

		TestUtil.serializeWorld(TEMP_FILE, true, world);

		long t1 = System.nanoTime();

		World loaded = TestUtil.deserializeWorld(TEMP_FILE);

		long t2 = System.nanoTime();

        System.out.printf("Large serialization total W=%s, R=%s, size=%s%n",
				StringUtil.formatTime(t1-t0, TimeUnit.NANOSECONDS),
				StringUtil.formatTime(t2-t1, TimeUnit.NANOSECONDS),
				StringUtil.formatMemoryAmount(TEMP_FILE.length()));
		double perEntityMult = 1.0 / (sceneCount * entityCount);
        System.out.printf("Large serialization per entity W=%s, R=%s, size=%s%n",
				StringUtil.formatTime(Math.round((t1-t0) * perEntityMult), TimeUnit.NANOSECONDS),
				StringUtil.formatTime(Math.round((t2-t1) * perEntityMult), TimeUnit.NANOSECONDS),
				StringUtil.formatMemoryAmount(Math.round(TEMP_FILE.length() * perEntityMult)));

		for (Scene scene : world.getScenes()) {
			TestUtil.assertEntitiesEqual(scene.getEntities(), loaded.getScene(scene.getId()).getEntities());
		}
	}

}
