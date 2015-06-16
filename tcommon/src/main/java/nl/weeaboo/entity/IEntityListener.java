package nl.weeaboo.entity;

public interface IEntityListener {

	public void onEntityCreated(Entity e);
	
	public void onEntityAttached(Scene s, Entity e);
	
	public void onEntityDetached(Scene s, Entity e);

	public void onEntityDestroyed(Entity e);
	
}
