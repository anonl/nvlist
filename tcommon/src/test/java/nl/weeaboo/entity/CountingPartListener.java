package nl.weeaboo.entity;

class CountingPartListener implements IPartListener {

	public int attached;
	public int detached;
	public int propertyChanges;
	
	@Override
	public void onPartAttached(Entity e, Part p) {
		attached++;
	}

	@Override
	public void onPartDetached(Entity e, Part p) {
		detached++;
	}

	@Override
	public void onPartPropertyChanged(Entity e, Part p, String propertyName, Object newValue) {
		propertyChanges++;
	}
	
}