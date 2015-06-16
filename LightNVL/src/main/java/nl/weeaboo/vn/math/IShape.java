package nl.weeaboo.vn.math;

import nl.weeaboo.common.Rect2D;

public interface IShape {

	//Functions
	public boolean contains(double x, double y);
	
	//Getters
	public Rect2D getBoundingRect();
	
	//Setters
	
}
