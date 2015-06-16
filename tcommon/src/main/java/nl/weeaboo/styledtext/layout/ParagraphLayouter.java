package nl.weeaboo.styledtext.layout;

import java.text.BreakIterator;
import java.util.Locale;

import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public class ParagraphLayouter {
	
	public ParagraphLayouter() {
	}
	
	public TextLayout doLayout(IGlyphStore glyphStore, StyledText stext, TextStyle defaultStyle, float width,
			float lineSpacing, boolean isRightToLeft)
	{
		MutableStyledText newText = stext.mutableCopy();
		newText.setBaseStyle(defaultStyle);
		stext = newText.immutableCopy();
		
		Locale locale = Locale.ROOT;
		BreakIterator charItr = BreakIterator.getCharacterInstance(locale);
		BreakIterator wordItr = BreakIterator.getLineInstance(locale); //new DefaultLineBreakIterator(charItr);

		LayoutRunHandler lrh = new LayoutRunHandler(glyphStore, width, lineSpacing, isRightToLeft, charItr);
		TextSplitter.run(lrh, stext, wordItr, isRightToLeft);
		return lrh.getTextLayout();
	}
	
}
