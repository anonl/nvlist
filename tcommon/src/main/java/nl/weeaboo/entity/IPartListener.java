package nl.weeaboo.entity;

public interface IPartListener {

	public void onPartAttached(Entity e, Part p);
	
	public void onPartDetached(Entity e, Part p);
	
	public void onPartPropertyChanged(Entity e, Part p, String propertyName, Object newValue);
	
}
