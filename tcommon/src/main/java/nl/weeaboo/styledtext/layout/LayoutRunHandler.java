package nl.weeaboo.styledtext.layout;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public class LayoutRunHandler implements TextSplitter.RunHandler {

	private final List<IGlyph> GLYPH_TEMP = new ArrayList<IGlyph>();
	private final List<LineElement> stack = new ArrayList<LineElement>();

	private final IGlyphStore glyphStore;
	private final float wrapWidth;
	private final boolean isRightToLeft;
	private final BreakIterator charItr;
	
	private TextLayout tl;
	private LineLayout ll;
	
	public LayoutRunHandler(IGlyphStore gs, float width, float lineSpacing, boolean isRightToLeft,
			BreakIterator charItr)
	{
		this.glyphStore = gs;
		this.wrapWidth = width;
		this.isRightToLeft = isRightToLeft;
		this.charItr = charItr;
				
		tl = new TextLayout(lineSpacing);
		ll = new LineLayout(isRightToLeft);
	}
	
	private void addElement(LineElement elem) {
		stack.add(elem);
		while (!stack.isEmpty()) {
			elem = stack.remove(stack.size()-1);
			
			final float ww = elem.getWidth();
			final boolean isSpace = elem.isSpace();			
			boolean fits = isSpace || wrapWidth <= 0 || ll.getWidth() + ww <= wrapWidth;
			
			if (fits) {
				//Still fits on this line
				//System.out.printf(StringUtil.LOCALE, "+ %03d-%03d: %s\n", Math.round(ll.getWidth()), Math.round(ll.getWidth()+elem.getWidth()), elem);
				ll.addElement(elem);
			} else {
				//Word doesn't fit (entirely) on the current line				
				if (ll.isEmpty() || (wrapWidth > 0 && elem.getWidth() > wrapWidth)) {
					LineElement[] parts = elem.split(null, wrapWidth - ll.getWidth());
					
					//Use whatever still fits on this line
					elem = parts[0];
					if (elem != null) {
						//System.out.printf(StringUtil.LOCALE, "* %03d-%03d: %s\n", Math.round(ll.getWidth()), Math.round(ll.getWidth()+elem.getWidth()), elem);
						ll.addElement(elem);
					}
					
					//Add leftover parts to queue
					for (int n = parts.length-1; n >= 1; n--) {
						if (parts[n] != null) {
							stack.add(parts[n]);
						}
					}			
				} else {
					stack.add(elem);
				}
			}
			
			if (!fits || (elem != null && elem.isLineBreaking())) {				
				//Start new line
				tl.addLine(ll);
				ll = new LineLayout(isRightToLeft);
			}
		}
	}
	
	@Override
	public void processRun(StyledText str, int start, int end, TextStyle style,
			boolean isWhitespace, boolean isLineBreak, int bidiLevel)
	{
		if (style == null) style = TextStyle.defaultInstance();
		
		//Collect glyphs
		charItr.setText(str.getCharacterIterator(start, end));
		float w = 0, h = 0;
		while (charItr.current() != BreakIterator.DONE) {
			int ccur = charItr.current();
			int cnext = charItr.next();
			if (cnext == BreakIterator.DONE) {
				break;
			}
			
			IGlyph g;
			if (cnext > ccur + 1) {
				g = glyphStore.getGlyph(style, str.substring(start+ccur, start+cnext).toString());
			} else {
				g = glyphStore.getGlyph(style, str.charAt(start+ccur));
			}
			
			w += g.getLayoutWidth();
			h = Math.max(h, g.getLineHeight());
			GLYPH_TEMP.add(g);
		}
		
		if (isWhitespace) {
			addElement(new Spacing(w, glyphStore.getLineHeight(style), style, isLineBreak, bidiLevel));
		} else {
			IGlyph[] gs = GLYPH_TEMP.toArray(new IGlyph[GLYPH_TEMP.size()]);
			addElement(new Word(str.substring(start, end), style, gs, w, h, bidiLevel));
			if (isLineBreak) {
				addElement(new Spacing(0, glyphStore.getLineHeight(style), style, true, bidiLevel));
			}
		}
		
		GLYPH_TEMP.clear();
	}
	
	public TextLayout getTextLayout() {
		if (ll != null && !ll.isEmpty()) {
			tl.addLine(ll);
			ll = null;
		}
		tl.seal(wrapWidth);
		return tl;
	}
	
}
