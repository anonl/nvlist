package nl.weeaboo.entity;

class ModelPart extends Part {

	private static final long serialVersionUID = 2L;

	private int x, y, z;
	
	public ModelPart() {
	}
	public ModelPart(int x, int y, int z) {
		this();
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	
	public void setX(int x) {
		if (this.x != x) {
			this.x = x;
			firePropertyChanged("x", x);
		}
	}
	public void setY(int y) {
		if (this.y != y) {
			this.y = y;
			firePropertyChanged("y", y);
		}
	}
	public void setZ(int z) {
		if (this.z != z) {
			this.z = z;
			firePropertyChanged("z", z);
		}
	}
	
}
