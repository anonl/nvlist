package nl.weeaboo.vn.core;


public interface IDrawablePart extends IRenderable {

	/**
	 * @return The current color (red, green, blue) and alpha, packed together into a single int in ARGB
	 *         order.
	 */
	public int getColorARGB();

	/**
	 * @return The current color (red, green, blue), packed into an int in RGB order. The value of the highest
	 *         8 bits is undefined.
	 */
	public int getColorRGB();

	public double getRed();
	public double getGreen();
	public double getBlue();
	public double getAlpha();

	/**
	 * @see #isVisible(double)
	 */
	@Override
	public boolean isVisible();

	/**
	 * A utility method to check if <code>visible && getAlpha() >= minAlpha</code>.
	 * @param minAlpha The minimum alpha to consider 'visible'.
	 * @return <code>true</code> if this drawable is visible and at least matches the minimum alpha.
	 */
	public boolean isVisible(double minAlpha);

	public BlendMode getBlendMode();
	public boolean isClipEnabled();

	/**
	 * Checks if the specified X/Y point lies 'inside' this renderable. What's considered inside may be
	 * different depending on the type of renderable.
	 *
	 * @param cx The X-coordinate of the point to test.
	 * @param cy The Y-coordinate of the point to test.
	 * @return <code>true</code> if the point is contained within this renderable.
	 */
	public boolean contains(double cx, double cy);

	/**
	 * @return The layer to which this drawable is attached, or {@code null} if not currently attached to
	 *         anything.
	 */
	public ILayer getParentLayer();

	public void setX(double x); //Calls setPos
	public void setY(double y); //Calls setPos
	public void setZ(short z);
	public void setWidth(double w); //Calls setSize
	public void setHeight(double h); //Calls setSize
	public void setPos(double x, double y);
	public void setSize(double w, double h);

	/**
	 * Moves and stretches this drawable to make it fit inside the specified bounding box.
	 */
	public void setBounds(double x, double y, double w, double h);

	public void setColor(double r, double g, double b); //Calls setColor(r, g, b, a)
	public void setColor(double r, double g, double b, double a);
	public void setColorRGB(int rgb); //Calls setColor(r, g, b, a)
	public void setColorARGB(int argb); //Calls setColor(r, g, b, a)
	public void setAlpha(double a); //Calls setColor(r, g, b, a)
	public void setVisible(boolean v);

	public void setBlendMode(BlendMode m);
	public void setClipEnabled(boolean clip);

}
