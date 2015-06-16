package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.weeaboo.styledtext.TextStyle;

public class LineLayout {

	private static final String SHY_STRING = "\u00AD";
	
	private final boolean isRightToLeft;
	
	private List<LineElement> mutableElements;		
	private float width, height;
	private float padLeft, padRight;
	private int glyphCount;
	
	private boolean sealed;	
	private LineElement[] elements;
	private int[] glyphOffsets;
	
	public LineLayout(boolean isRightToLeft) {
		this.isRightToLeft = isRightToLeft;
		
		mutableElements = new ArrayList<LineElement>(8);
	}
	
	//Functions	
	public void addElement(LineElement elem) {
		if (isSealed()) throw new IllegalStateException("Invalid operation after sealing");

		if (!elem.isSpace() && !mutableElements.isEmpty()) {
			//Find last word and remove all SHY from it
			ListIterator<LineElement> litr = mutableElements.listIterator(mutableElements.size());
			while (litr.hasPrevious()) {
				LineElement le = litr.previous();
				if (le.isWord()) {
					Word word = (Word)le;
					float ww = word.getWidth();
					word = removeSHY(word, true);
					width += word.getWidth() - ww; //Update total width after word's possible width change
					litr.set(word);
					break;
				}
			}
		}
				
		if (elem.isWord()) {
			Word word = (Word)elem;			
			word = removeSHY(word, false);		
			glyphCount += word.getGlyphCount();
		}

		mutableElements.add(elem);		
		width += elem.getWidth();
		height = Math.max(height, elem.getHeight());		
	}
	
	private static Word removeSHY(Word word, boolean alsoRemoveTrailing) {
		return word.withoutGlyphs(SHY_STRING, 0, word.getGlyphCount() - (alsoRemoveTrailing ? 0 : 1));
	}
	
	public void seal(float lineWidth) {
		if (sealed) return;
		
		int numGaps = 0;
		{ //Add spacing elements to generate the required padding for centered/right aligned text
			List<LineElement> newlist = new ArrayList<LineElement>(mutableElements.size());

			int halign = (isRightToLeft ? 1 : -1);
			width = 0;
			for (LineElement elem : mutableElements) {
				if (elem.isWord()) {
					TextStyle style = elem.getStyle();
					int halign2 = TextStyle.getHorizontalAlign(style.getAnchor(), isRightToLeft);					
					if (halign != halign2) {
						newlist.add(null);
						numGaps++;
						halign = halign2;
					}
				}
				newlist.add(elem);
				width += elem.getWidth();
			}
			removeTrailingWhitespace(newlist);
			if (halign != (isRightToLeft ? -1 : 1)) {
				newlist.add(null);
				numGaps++;
			}
			
			mutableElements = newlist;
		}
		
		elements = mutableElements.toArray(new LineElement[mutableElements.size()]);		
		mutableElements = null;
		
		{ //Assign widths to the spacing elements
			float freeSpace = (lineWidth > 0 ? lineWidth - width : 0);
			final float gapSize = (numGaps > 0 ? freeSpace / numGaps : 0);
			Spacing spacing = null;

			for (int n = 0; n < elements.length; n++) {
				LineElement elem = elements[n];
				if (elem == null) {
					float sw = Math.min(freeSpace, gapSize);
					if (spacing == null || spacing.getWidth() != sw) {
						spacing = new Spacing(sw, 0, TextStyle.defaultInstance(), false, isRightToLeft ? -1 : 0);					
					}
					elements[n] = spacing;
					freeSpace -= sw;
				}
			}
		}
		
		glyphOffsets = calculateGlyphOffsets(elements, 0, elements.length);		
		bidiVisualSortElements();
				
		//Determine left/right padding
		float extent = 0;
		float startPos = 0;
		float endPos = 0;
		boolean hasSeenNonSpace = false;
		for (int n = 0; n < elements.length; n++) {
			LineElement elem = elements[n];
			final boolean isSpace = elem.isSpace();
			if (!isSpace && !hasSeenNonSpace) {
				startPos = extent;
				hasSeenNonSpace = true;
			}
			extent += elem.getWidth();
			if (!isSpace) {
				endPos = extent;
			}
		}
		
		padLeft = startPos;
		padRight = extent - endPos;
		width = lineWidth - padLeft - padRight;
				
		sealed = true;
	}
	
	private void removeTrailingWhitespace(List<LineElement> list) {
		ListIterator<LineElement> litr = list.listIterator(list.size());
		while (litr.hasPrevious()) {
			LineElement elem = litr.previous();
			if (elem == null) {
				//Ignore?
			} else if (elem.isSpace()) {
				width -= elem.getWidth();
				litr.remove();
			} else {
				break;
			}
		}
	}
	
	private static int[] calculateGlyphOffsets(LineElement[] elements, int off, int len) {
		int[] offsets = new int[len];
		
		int glyphCount = 0;
		for (int n = 0; n < len; n++) {
			LineElement elem = elements[off+n];
			offsets[n] = glyphCount;
			if (elem.isWord()) {
				Word word = (Word)elem;
				glyphCount += word.getGlyphCount();
			}
		}
		
		return offsets;
	}
	
	private void bidiVisualSortElements() {
		final int len = elements.length;
		
		int lo = 63; //Must be odd and above the max level used by the Java Bidi class (which is 62)
		int hi = 0;
		for (int n = 0; n < len; n++) {
			int level = elements[n].getBidiLevel();
			hi = Math.max(hi, level);
			if ((level & 1) != 0) {
				lo = Math.min(lo, level);
			}
		}

		while (hi >= lo) {
			int n = 0;
			while (true) {
				while (n < len && elements[n].getBidiLevel() < hi) {
					n++;
				}
				
				int start = n;
				n++;
				
				if (start >= len) {
					break;
				}

				while (n < len && elements[n].getBidiLevel() >= hi) {
					n++;
				}
				int end = n - 1;

				//Exchange elements
				while (start < end) {
					swapElements(start, end);
					start++;
					end--;
				}
			}
			hi--;
		}
	}
	
	private void swapElements(int a, int b) {
		LineElement tempElement = elements[a];
		int tempGlyphOffset = glyphOffsets[a];
		elements[a] = elements[b];
		glyphOffsets[a] = glyphOffsets[b];
		elements[b] = tempElement;
		glyphOffsets[b] = tempGlyphOffset;
	}
	
	private void checkSealed() {
		if (!sealed) throw new IllegalStateException("LineLayout must be sealed first");
	}
	
	//Getters
	public int getGlyphCount() { return glyphCount; }
	
	/**
	 * Total width including leading/trailing whitespace.
	 */
	public float getPaddedWidth() { return padLeft + width + padRight; }
	
	/**
	 * Width excluding leading/trailing whitespace. Any whitespace between words does count for the width.
	 */
	public float getWidth() { return width; }
	
	/**
	 * Logical line height for layout purposes, isn't necessarily filled with content. 
	 */
	public float getHeight() { return height; }
	
	/**
	 * The x-offset to the first non-whitespace.
	 */
	public float getLeft() { return padLeft; }
	
	/**
	 * The x-offset to the end of the last non-whitespace. 
	 */
	public float getRight() { return padLeft + width; }
	
	/**
	 * Leading whitespace. 
	 */
	public float getPadLeft() { return padLeft; }
	
	/**
	 * Trailing whitespace. 
	 */
	public float getPadRight() { return padRight; }
	
	public boolean isSealed() {
		return sealed;
	}
	
	public boolean isRightToLeft() {
		return isRightToLeft;
	}
	
	public boolean isEmpty() {
		return (sealed ? elements.length == 0 : mutableElements.isEmpty());
	}	
	
	public int getElementCount() {
		return (sealed ? elements.length : mutableElements.size());
	}
	
	public LineElement getElement(int index) {
		return (sealed ? elements[index] : mutableElements.get(index));
	}
		
	public int getGlyphOffset(int index) {
		checkSealed();
		return glyphOffsets[index];
	}
	
	public int getElementIndexAt(float cx) {
		//System.out.println(cx + " " + sealWidth + " " + padLeft + " " + width + " " + padRight);		
		float x = 0;
		for (int n = 0; n < getElementCount(); n++) {
			LineElement le = getElement(n);
			float elemW = le.getWidth();
			if (cx >= Math.min(x, x+elemW) && cx < Math.max(x, x+elemW)) {
				return n;
			}
			x += elemW;
		}
		return -1;
	}
	
	//Setters
	
}
