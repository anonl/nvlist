package nl.weeaboo.styledtext.layout;


public class LayoutTest {
/*
	//static final StyledText str = new StyledText("שהר שהר שהר aaa שהר שהר שהרשהרשהרשהר", new TextStyle("SansSerif", FontStyle.PLAIN, 12, 9));
	//static final StyledText str = new StyledText("abc Test שהרשהרשהרשהרשהר (Test2)\n\n", new TextStyle("SansSerif", FontStyle.PLAIN, 12, 9));
	//static final StyledText str = new StyledText("\u05e9\u05dc 1234 \u05d5\u05dd WORD \u05e9\u05dc!@#$%^&*()\u05d5\u05dd");	
	//static final StyledText str = new StyledText("De kat krabt de krullen van de trap");
	static final StyledText str = new StyledText("De kat krabt de krullen van de trap", new TextStyle("SansSerif", FontStyle.PLAIN, 12));	
	
	static final int GS = 10;
	static final int RUNS = 1000;
	static final int wrapWidth = 10 * GS;
	static final boolean RIGHT_TO_LEFT = false;
	
	public static void main(String[] args) {
		MutableStyledText mts = str.mutableCopy();
		//mts.extendStyle(3, 6, TextStyle.withTags(1, 2));
		StyledText stext = mts.immutableCopy();
		
		ExtensibleGlyphStore glyphStore = new ExtensibleGlyphStore(new TestGlyphStore(GS));
		glyphStore.setOverride(1, new TestGlyphStore(4 * GS));
		
		Benchmark.tick();
		TextLayout tl = null;
		for (int run = 0; run < RUNS; run++) {
			BreakIterator lineBreaks = BreakIterator.getLineInstance(StringUtil.LOCALE);
			TestRunHandler rh = new TestRunHandler(glyphStore);
			TextSplitter.run(rh, stext, lineBreaks, true);
			tl = rh.getTextLayout();
		}
		long durationNS = Benchmark.tock(false);
		
		if (tl != null) {
			for (LineLayout ll : tl) {
				System.out.println("*LINE");
				float x = 0;
				for (int n = 0; n < ll.getElementCount(); n++) {
					LineElement elem = ll.getElement(n);
					TextStyle style = elem.getStyle();
					System.out.printf("%03d: %d %s %s%n", Math.round(x), elem.getBidiLevel(), Arrays.toString(style.getTags()), elem);
					x += elem.getWidth();
				}
				//System.out.println(" -> " + ll.getElementIndexAt(33f));
				System.out.printf(StringUtil.LOCALE, "LEFT=%.1f, RIGHT=%.1f%n", ll.getPadLeft(), ll.getPadRight());
			}
		}
		System.out.printf(StringUtil.LOCALE, "%.2fms per run%n", durationNS * .000001 / Math.max(1, RUNS));
	}
		
	private static String toEscapedString(int codepoint) {
		return toEscapedString(new String(Character.toChars(codepoint)));
	}
	private static String toEscapedString(StyledText stext) {
		return toEscapedString(stext.toString());		
	}
	private static String toEscapedString(String str) {
		if (str.contains("\n")) {
			return str.replace("\n", "\\n");
		} else {
			return str;
		}
	}
	
	private static class TestRunHandler extends LayoutRunHandler {

		public TestRunHandler(IGlyphStore glyphStore) {
			super(glyphStore, wrapWidth, 0, RIGHT_TO_LEFT, BreakIterator.getCharacterInstance(StringUtil.LOCALE));
		}
	}
		
	private static class TestGlyphStore implements IGlyphStore {
		
		final int gs;
		
		public TestGlyphStore(int gs) {
			this.gs = gs;
		}
		
		@Override
		public IGlyph getGlyph(TextStyle style, int codepoint) {
			return new TestGlyph(toEscapedString(codepoint), gs, gs);
		}

		@Override
		public IGlyph getGlyph(TextStyle style, String str) {
			return new TestGlyph(toEscapedString(str), gs, gs);
		}

		@Override
		public float getLineHeight(TextStyle style) {
			return getGlyph(style, ' ').getLineHeight();
		}
	
	}
	
	@SuppressWarnings("unused")
	private static class PrintRunHandler implements TextSplitter.RunHandler {

		@Override
		public void processRun(StyledText str, int start, int end, TextStyle style,
				boolean isWhitespace, boolean isLineBreak, int bidiLevel)
		{
			System.out.printf("start=%03d, end=%03d, whitespace=%d, newline=%d, bidiLevel=%d :: %s\n",
					start, end, isWhitespace?1:0, isLineBreak?1:0, bidiLevel,
							toEscapedString(str.substring(start, end)));
		}
		
	}
	
	private static class TestGlyph extends AbstractGlyph {

		public TestGlyph(String chars, float w, float h) {
			super(chars, new Area2D(0, 0, w, h), w, h);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TestGlyph) {
				TestGlyph tg = (TestGlyph)obj;
				return chars.equals(tg.chars);
			}
			return false;
		}
		
	}
	*/
}
