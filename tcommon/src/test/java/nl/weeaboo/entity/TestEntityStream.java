package nl.weeaboo.entity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class TestEntityStream {

	private static Entity createEntity(TestPartRegistry pr, Scene s, int val) {
		Entity e = s.createEntity();

		e.addPart(pr.typeA, new ModelPart(val, val+1, val+2));

		return e;
	}

	/**
	 * Tests an unsorted stream.
	 */
	@Test
	public void unsortedStream() {
		final TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		Scene scene = world.createScene();

		createEntity(pr, scene, 1);
		createEntity(pr, scene, 4);
		createEntity(pr, scene, 7);

		ESD<ModelPart> esd = new ESD<ModelPart>(pr.typeA);
		esd.sortDirection = 0;

		EntityStream alpha = scene.joinStream(esd);
		Assert.assertEquals(scene.getEntitiesCount(), alpha.count());
	}

	@Test
	public void sortedStream() {
		final TestPartRegistry pr = new TestPartRegistry();
		World world = new World(pr);
		Scene scene = world.createScene();

		createEntity(pr, scene, 1);
		createEntity(pr, scene, 4);
		createEntity(pr, scene, 7);

		ESD<ModelPart> esd = new ESD<ModelPart>(pr.typeA);
		esd.sortDirection = 1;

		EntityStream alpha = scene.joinStream(esd);
		assertOrdered(alpha, esd);

		esd.sortDirection = -1;
		alpha.invalidate();
		assertOrdered(alpha, esd);

		esd.requiredParts.add(pr.typeB);
	}

	private static <T> void assertOrdered(Iterable<T> iterable, Comparator<T> comparator) {
		T prev = null;
		for (T val : iterable) {
			if (prev != null) {
				if (comparator.compare(prev, val) > 0) {
					Assert.fail("Incorrect sort order");
				}
			}
			prev = val;
		}
	}

	// Inner classes
	private static class ESD<T extends ModelPart> extends EntityStreamDef {

		private final PartType<T> comparePartType;

		int sortDirection = 1;
		Set<PartType<?>> requiredParts = new HashSet<PartType<?>>();

		public ESD(PartType<T> comparePartType) {
			this.comparePartType = comparePartType;
		}

		@Override
		public int compare(Entity a, Entity b) {
			ModelPart aPart = a.getPart(comparePartType);
			ModelPart bPart = b.getPart(comparePartType);
			int c = (aPart.getX() < bPart.getX() ? -1 : (aPart.getX() == bPart.getX() ? 0 : 1));
			return sortDirection * c;
		}

		@Override
		public boolean accept(Entity e) {
			for (PartType<?> type : requiredParts) {
				if (!e.hasPart(type)) {
					return false;
				}
			}
			return true;
		}

	}

}
