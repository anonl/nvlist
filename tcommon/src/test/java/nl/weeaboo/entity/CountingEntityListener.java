package nl.weeaboo.entity;

class CountingEntityListener implements IEntityListener {

	public int created;
	public int attached;
	public int detached;
	public int destroyed;
	
	@Override
	public void onEntityCreated(Entity e) {
		created++;
	}

	@Override
	public void onEntityAttached(Scene s, Entity e) {
		attached++;
	}

	@Override
	public void onEntityDetached(Scene s, Entity e) {
		detached++;
	}

	@Override
	public void onEntityDestroyed(Entity e) {
		destroyed++;
	}
	
}