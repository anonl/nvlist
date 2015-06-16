package nl.weeaboo.styledtext.render;

class AbstractRenderInfo {

	private float cursorX, cursorY;
	private float x, y, w, h; 
	
	protected AbstractRenderInfo() {
	}
	
	//Functions
	final void init(float cx, float cy, float bx, float by, float bw, float bh) {
		this.cursorX = cx;
		this.cursorY = cy;
		this.x = bx;
		this.y = by;
		this.w = bw;
		this.h = bh;
	}	
	
	//Getters
	public float getCursorX() { return cursorX; }
	public float getCursorY() { return cursorY; }
	
	public float getX() { return x; }
	public float getY() { return y; }
	public float getWidth() { return w; }
	public float getHeight() { return h; }
	
	//Setters
	
}
