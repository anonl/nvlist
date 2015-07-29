package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.render.IDrawBuffer;

public interface ITextRenderer extends IUpdateable, IDestructible, Serializable {

	//Functions
	public float increaseVisibleChars(float textSpeed);

	public void draw(IDrawBuffer buf, short z, boolean clipEnabled, BlendMode blendMode, int argb,
			double dx, double dy);

	//Getters
	public int getEndLine();
	public int getLineCount();
	public int getCharOffset(int line);
	public boolean isRightToLeft();

	public float getMaxWidth();
	public float getMaxHeight();

	/**
	 * @see #getTextWidth(int, int)
	 */
	public float getTextWidth();

	/**
	 * Returns the minimum bounding text width, excluding any leading or trailing whitespace.
	 */
	public float getTextWidth(int startLine, int endLine);

	/**
     * @see #getTextHeight(int, int)
     */
	public float getTextHeight();

	/**
     * Returns the minimum bounding text height.
     */
	public float getTextHeight(int startLine, int endLine);

	/**
	 * @see #getTextLeading(int, int)
	 */
	public float getTextLeading();

	/**
	 * Returns the minimum amount of leading whitespace for the specified range of lines.
	 */
	public float getTextLeading(int startLine, int endLine);

	/**
	 * @see #getTextTrailing(int, int)
	 */
	public float getTextTrailing();

	/**
	 * Returns the minimum amount of trailing whitespace for the specified range of lines.
	 */
	public float getTextTrailing(int startLine, int endLine);

	/**
	 * Returns the minimum bounding width of the specified line;
	 */
	public float getLineWidth(int line);

	/**
	 * Returns the tags of the TextStyle of the characters at the specified location or <code>null</code> if no hit.
	 */
	public int[] getHitTags(float cx, float cy);

	//Setters
	public void setMaxSize(float w, float h);
	public void setText(StyledText stext);
	public void setVisibleText(int startLine, float visibleChars);
	public void setDefaultStyle(TextStyle ts);
	public void setCursor(IDrawablePart cursor);
	public void setRenderEnv(IRenderEnv env);
	public void setRightToLeft(boolean rtl);

}
