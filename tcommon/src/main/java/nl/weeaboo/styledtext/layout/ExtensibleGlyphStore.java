package nl.weeaboo.styledtext.layout;

import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.styledtext.TextStyle;

public class ExtensibleGlyphStore implements IGlyphStore {

	private IGlyphStore backing;
	private Map<Integer, IGlyphStore> overrides;
	
	public ExtensibleGlyphStore(IGlyphStore backing) {
		this.backing = backing;
		this.overrides = new HashMap<Integer, IGlyphStore>();
	}
	
	public IGlyphStore getBacking() {
		return backing;
	}
	
	private IGlyphStore getStore(TextStyle style) {
		if (!overrides.isEmpty()) {
			int[] tags = style.getTags();
			if (tags != null && tags.length > 0) {
				for (int tag : tags) {
					IGlyphStore store = overrides.get(tag);
					if (store != null) {
						return store;
					}
				}
			}
		}
		return backing;
	}
	
	@Override
	public IGlyph getGlyph(TextStyle style, int codepoint) {
		return getStore(style).getGlyph(style, codepoint);
	}

	@Override
	public IGlyph getGlyph(TextStyle style, String chars) {
		return getStore(style).getGlyph(style, chars);
	}

	@Override
	public float getLineHeight(TextStyle style) {
		return getStore(style).getLineHeight(style);
	}

	public void setOverride(int tag, IGlyphStore store) {
		overrides.put(tag, store);
	}
	
	public void setBacking(IGlyphStore gs) {
		backing = gs;
	}
	
}
