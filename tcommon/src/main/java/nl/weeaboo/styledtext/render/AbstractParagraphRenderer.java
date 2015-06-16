package nl.weeaboo.styledtext.render;

import java.util.ArrayDeque;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.FontStyle;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ExtensibleGlyphStore;
import nl.weeaboo.styledtext.layout.IGlyph;
import nl.weeaboo.styledtext.layout.IGlyphStore;
import nl.weeaboo.styledtext.layout.LineElement;
import nl.weeaboo.styledtext.layout.LineLayout;
import nl.weeaboo.styledtext.layout.ParagraphLayouter;
import nl.weeaboo.styledtext.layout.TextLayout;
import nl.weeaboo.styledtext.layout.Word;

public abstract class AbstractParagraphRenderer<G> {

	public static TextStyle DEFAULT_STYLE = new TextStyle("SansSerif", FontStyle.PLAIN, 12);
	
	protected final ParagraphLayouter layouter;
	protected final IGlyphRenderBuffer<G> grb;
	protected final MutableRenderInfo lineRI = new MutableRenderInfo();
	protected final MutableRenderInfo wordRI = new MutableRenderInfo();
	
	protected ExtensibleGlyphStore glyphStore;
	protected IGlyphRenderer glyphRenderer;
	protected Rect2D bounds;
	protected TextStyle defaultStyle;
	protected boolean isRightToLeft;
	
	protected int lineSpacing;
	protected int lineOffset;
	protected float visibleChars;
	protected int visibleLines;
		
	private ArrayDeque<LineElement> TEMP;
	
	public AbstractParagraphRenderer(ParagraphLayouter layouter, IGlyphRenderer gr, IGlyphRenderBuffer<G> grb,
			ExtensibleGlyphStore gs)
	{		
		this.layouter = layouter;
		this.grb = grb;
		
		glyphStore = gs;
		glyphRenderer = gr;
		bounds = Rect2D.EMPTY;
		defaultStyle = DEFAULT_STYLE;
		
		lineSpacing = 0;
		lineOffset = 0;
		visibleChars = visibleLines = -1;
		
		TEMP = new ArrayDeque<LineElement>();
	}
	
	//Functions
	public RenderInfo drawText(G glm, String text) {
		return drawText(glm, new StyledText(text));
	}
	public RenderInfo drawText(G glm, StyledText stext) {
		return drawLayout(glm, getLayout(stext), 0, 0);
	}
	public RenderInfo drawLayout(G g, TextLayout tl, float dx, float dy) {
		return drawLayout(g, tl, lineOffset, visibleLines, visibleChars, getX()+dx, getY()+dy);
	}
		
	protected RenderInfo drawLayout(G g, TextLayout tl, int lineOffset, int visibleLines,
			float visibleChars, float x, float y)
	{
		grb.setColor(g);
		
		RenderInfo info = null;
		for (TextLayer layer : TextLayer.values()) {			
			RenderInfo ri = drawLayout(tl, lineOffset, visibleLines, visibleChars, x, y, layer);
			if (layer == TextLayer.FOREGROUND) {
				info = ri;
			}
		}
		
		grb.flush(g);
		
		return info;
	}
	
	protected RenderInfo drawLayout(TextLayout tl, int lineOffset, int visibleLines, float visibleChars,
			float x, float y, TextLayer layer)
	{
		float cx = 0;
		float cy = 0;
		float bw = 0;
		float bh = 0;
		
		float ty = y;
		
		int line = 0;
		int charIndex = 0;
		for (LineLayout ll : tl) {
			//System.out.println("- LINE - " + x + " " + tl.getWidth() + " " + ll.getPaddedWidth());
			
			if (line >= lineOffset && (visibleLines < 0 || line < lineOffset+visibleLines)) {
				int lineLength = ll.getGlyphCount();
				float lineTo = lineLength;
				if (visibleChars >= 0) {
					lineTo = Math.min(lineLength, visibleChars - charIndex);
				}
				
				if (lineTo > 0) {
					drawLineLayout(lineRI, ll, lineTo, x, ty, layer);				
					cx = lineRI.getCursorX();
					cy = lineRI.getCursorY();
					bw = Math.max(bw, lineRI.getWidth());
					bh = ty + ll.getHeight() - y;				
				}

				ty += ll.getHeight() + lineSpacing;				
				charIndex += lineLength;				
			}
			line++;
		}
		
		return new RenderInfo(cx, cy, x, y, bw, bh);
	}
	
	protected void drawLineLayout(MutableRenderInfo lineRI, LineLayout ll, float visible,
			float x, float y, TextLayer tl)
	{
		if (!ll.isSealed()) throw new IllegalStateException("Invalid operation before sealing");
		
		final float initialX = x;		
		final float lineHeight = ll.getHeight();
		
		// Within a span, the glyph offsets shouldn't have any unexplained gaps.
		// Counting from the first element in the span, the last element's glyph
		// offset should be deducible without asking the LineLayout.
				
		int bidiLevel = 0;
		int glyphOffset = Integer.MAX_VALUE;
		float spanW = 0;
		for (int n = 0; n < ll.getElementCount(); n++) {
			LineElement elem = ll.getElement(n);
			int go = ll.getGlyphOffset(n);
			int bidi = elem.getBidiLevel();

			if (bidi != bidiLevel) {
				boolean isRTL = (bidiLevel&1) != 0;
				drawSpan(TEMP, isRTL, visible-glyphOffset, (isRTL ? x + spanW : x), y, lineHeight, tl);
				TEMP.clear();
				x += spanW;
				bidiLevel = bidi;
				glyphOffset = Integer.MAX_VALUE;
				spanW = 0;
			}

			if ((bidi&1) != 0) {
				TEMP.addFirst(elem); //We want to draw RTL, but the element ordering is LTR
			} else {
				TEMP.addLast(elem);
			}
			glyphOffset = Math.min(glyphOffset, go);
			spanW += elem.getWidth();
		}
		if (!TEMP.isEmpty()) {
			boolean isRTL = (bidiLevel&1) != 0;
			drawSpan(TEMP, isRTL, visible-glyphOffset, (isRTL ? x + spanW : x), y, lineHeight, tl);			
			TEMP.clear();
			x += spanW;
		}
		
		lineRI.set(x, y, Math.min(initialX, x), y, Math.abs(x - initialX), lineHeight);		
	}
	
	protected void drawSpan(Iterable<LineElement> elems, boolean isRightToLeft,
			float visibleChars, float x, float y, float lineHeight, TextLayer tl)
	{
		float lastUnderlineX = Float.NaN;
		float lastUnderlineY = Float.NaN;
		
		TextStyle currentStyle = null;
		IGlyphCache gc = null;
		IGlyphCache shadowGC = null;
		boolean currentStyleSkipShadow = false;
		boolean currentStyleUnderlined = false;
		int oldColor = grb.getColor();
				
		int charIndex = 0;
		//System.out.println("SPAN");
		for (LineElement elem : elems) {
			//System.out.println("DRAW " + isRightToLeft + " " + elem);
			
			TextStyle style = elem.getStyle();
			if (currentStyle != style) {
				currentStyle = style;
				
				gc = getGlyphCache(style);
				currentStyleSkipShadow = !style.hasShadow() || !gc.usesSeparateShadow();
				currentStyleUnderlined = style.isUnderlined();
				if (!currentStyleUnderlined) {
					lastUnderlineX = Float.NaN;
				}
								
				if (tl == TextLayer.SHADOW) {
					//Get shadow glyph cache
					MutableTextStyle sstyle = style.mutableCopy();
					sstyle.setColor(style.getShadowColor());
					sstyle.setOutlineColor(style.getShadowColor());
					shadowGC = getGlyphCache(sstyle.immutableCopy());
				
					//Change foreground color, relative to the base color
					grb.setColor(oldColor);
					int color = style.getShadowColor();
					if (color != 0xFFFFFFFF && shadowGC.canColorizeForeground()) {
						grb.mixColor(color);
					}					
				} else {
					//Change foreground color, relative to the base color
					grb.setColor(oldColor);
					int color = style.getColor();
					if (color != 0xFFFFFFFF && gc.canColorizeForeground()) {
						grb.mixColor(color);
					}					
				}
			}
			
			final float ew = (isRightToLeft ? -elem.getWidth() : elem.getWidth());
			if (elem.isWord()) {
				Word word = (Word)elem;

				final int wordLength = word.getGlyphCount();
				final float wordTo   = Math.min(wordLength, visibleChars - charIndex);				
				boolean skipDraw = (wordTo <= 0);
				
				float dx = 0;
				float dy = 0;
				IGlyphCache wordGlyphReplacer = null;
				
				//Handle shadow layer
				if (tl == TextLayer.SHADOW) {
					if (currentStyleSkipShadow) {
						skipDraw = true;
					} else {
						dx = style.getShadowDx();
						dy = style.getShadowDy();
						wordGlyphReplacer = shadowGC;
					}
				}
								
				if (!skipDraw) {
					//Determine word's vertical offset
					float yoffset = 0;
					int anchor = style.getAnchor();
					if (anchor >= 4 && anchor <= 6) {
						yoffset += (lineHeight - elem.getHeight()) * .5;
					} else if (anchor >= 1 && anchor <= 3) {
						yoffset += (lineHeight - elem.getHeight());
					}
					
					//System.out.println(y + " " + yoffset + " " +dy + " " + lineHeight + " " + elem.getHeight());
					
					//Draw word
					final float startX = x + dx;
					final float endX = startX + ew;					
					drawWordGlyphs(wordRI, word, wordTo, startX, endX, y+yoffset+dy, grb, glyphRenderer, wordGlyphReplacer);
					
					//Draw word underline
					if (currentStyleUnderlined) {
						float ulx0 = wordRI.getX();
						float ulx1 = ulx0 + wordRI.getWidth();
						float uly = y + yoffset + wordRI.getHeight();
						if (!skipDraw) {
							if (!Float.isNaN(lastUnderlineY) && Math.abs(lastUnderlineY - uly) <= 0.0001f) {
								grb.bufferGlyph(null, lastUnderlineX, uly, ulx0 - lastUnderlineX, 2);
							}								
							grb.bufferGlyph(null, ulx0, uly, ulx1-ulx0, 2);
						}
						lastUnderlineX = ulx1;
						lastUnderlineY = uly;
					}
				}	
				
				charIndex += wordLength;	
			}			
			x += ew;
		}
		grb.setColor(oldColor);	
	}
	
	private void drawWordGlyphs(MutableRenderInfo wordRI, Word word, float visible, float startX, float endX, float y,
			IGlyphRenderBuffer<G> grb, IGlyphRenderer glyphRenderer, IGlyphCache glyphReplacer)
	{
		visible = Math.min(visible, word.getGlyphCount());
				
		float cx = Math.min(startX, endX);
		float cy = y;
		
		float bx = Float.NaN;
		float by = Float.NaN;
		float bw = 0;
		float bh = 0;
		
		final boolean rtl = (word.getBidiLevel() & 1) != 0;		
		final int start, end, inc;
		if (rtl) {
			start = word.getGlyphCount()-1;
			end = -1;
			inc = -1;
		} else {
			start = 0;
			end = word.getGlyphCount();
			inc = 1;
		}
				
		for (int n = start; n != end; n += inc) {
			IGlyph glyph = word.getGlyph(n);
			if (glyphReplacer != null) {
				glyph = glyphReplacer.getGlyph(glyph.getChars());				
			}
			Area2D q = glyph.getVisualBounds();
			float layoutWidth = glyph.getLayoutWidth();
			
			if (n < visible) {
				if (grb != null && glyphRenderer != null) {
					double gw = q.w;
					double gx;
					if (rtl && glyph.isMirrorGlyph()) {
						gx = roundx(cx - q.x + gw);
						gw = -gw;
					} else {
						gx = roundx(cx + q.x);
					}
					double gy = roundy(y + q.y);
					double gh = q.h;
					
					glyphRenderer.drawGlyph(grb, glyph, gx, gy, gw, gh, visible, n);
				}
				
				if (Float.isNaN(bx)) bx = cx;
				if (Float.isNaN(by)) by = cy;
				bw += layoutWidth;
				bh = Math.max(bh, glyph.getLineHeight());
			}
			
			cx += layoutWidth;
		}
		
		wordRI.set(cx, cy, bx, by, bw, bh);
	}
	
	private static double roundx(double i) {
		return Math.round(i);
	}
	private static double roundy(double i) {
		return Math.round(i);
	}
	
	//Getters
	public IGlyphRenderer getGlyphRenderer() { return glyphRenderer; }
	public ExtensibleGlyphStore getGlyphStore() { return glyphStore; }
	public IGlyphStore getBackingGlyphStore() { return glyphStore.getBacking(); }
	
	protected abstract IGlyphCache getGlyphCache(TextStyle style);
	
	public Rect2D getBounds() { return bounds; }
	public float getX() { return (float)bounds.x; }
	public float getY() { return (float)bounds.y; }
	public float getWidth() { return (float)bounds.w; }
	public float getHeight() { return (float)bounds.h; }
	
	public TextLayout getLayout(StyledText stext) {
		return getLayout(stext, getWidth());
	}
	public TextLayout getLayout(StyledText stext, float width) {
		return layouter.doLayout(glyphStore, stext, defaultStyle, width, lineSpacing, isRightToLeft);		
	}

	public Rect2D getNaturalBounds(String s) {
		return getNaturalBounds(new StyledText(s));
	}
	public Rect2D getNaturalBounds(StyledText stext) {
		TextLayout tl = getLayout(stext, 0);				
		return Rect2D.of(bounds.x, bounds.y, tl.getWidth(), tl.getHeight());
	}
	
	public Rect2D getTextBounds(String s) {
		return getTextBounds(new StyledText(s));
	}
	public Rect2D getTextBounds(StyledText stext) {
		TextLayout tl = getLayout(stext, getWidth());
		return Rect2D.of(bounds.x, bounds.y, tl.getWidth(), tl.getHeight());
	}
	
	public int getLineSpacing() { return lineSpacing; }
	public float getVisibleChars() { return visibleChars; }
	public int getLineOffset() { return lineOffset; }
	public int getVisibleLines() { return visibleLines; }

	public TextStyle getDefaultStyle() { return defaultStyle; }
	public boolean isRightToLeft() { return isRightToLeft; }
	
	//Setters
	public void setBounds(double x, double y, double w, double h) { bounds = Rect2D.of(x, y, w, h); }
	public void setDefaultStyle(TextStyle style) { this.defaultStyle = style; }
	public void setRightToLeft(boolean rtl) { this.isRightToLeft = rtl; } 
	public void setLineSpacing(int lsp) { this.lineSpacing = lsp; }
	public void setVisibleChars(float visibleChars) { this.visibleChars = visibleChars; }
	public void setLineOffset(int lineOffset) { this.lineOffset = lineOffset; }
	public void setVisibleLines(int visibleLines) { this.visibleLines = visibleLines; }
	public void setGlyphRenderer(IGlyphRenderer gr) { this.glyphRenderer = gr; }	
	public void setBackingGlyphStore(IGlyphStore gs) { glyphStore.setBacking(gs); }
	
}
