package nl.weeaboo.vn.script.lvn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;

class StyleStack implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<TaggedEntry> stack;
	private transient TextStyle calculated;

	public StyleStack() {
		stack = new ArrayList<TaggedEntry>();
	}

	//Functions
	public void clear() {
		if (!stack.isEmpty()) {
			stack.clear();
			onStackChanged();
		}
	}

	public void pushWithTag(String tag, TextStyle style) {
		if (style == null) throw new IllegalArgumentException("Style may not be null");

		stack.add(new TaggedEntry(tag, style));
		onStackChanged();
	}

	public boolean popWithTag(String tag) {
		ListIterator<TaggedEntry> litr = stack.listIterator(stack.size());
		while (litr.hasPrevious()) {
			TaggedEntry entry  = litr.previous();
			if (tag == entry.tag || (tag != null && tag.equals(entry.tag))) {
				litr.remove();
				onStackChanged();
				return true;
			}
		}
		return false;
	}

	protected void onStackChanged() {
		calculated = null;
	}

	//Getters
	public TextStyle getCalculatedStyle() {
		if (calculated == null) {
			MutableTextStyle mts = new MutableTextStyle();
			for (TaggedEntry entry : stack) {
				mts.extend(entry.style);
			}
			calculated = mts.immutableCopy();
		}
		return calculated;
	}

	//Setters

	//Inner Classes
	private static class TaggedEntry implements Serializable {

		private static final long serialVersionUID = 1L;

		final String tag;
		final TextStyle style;

		public TaggedEntry(String tag, TextStyle style) {
			this.tag = tag;
			this.style = style;
		}

	}

}
