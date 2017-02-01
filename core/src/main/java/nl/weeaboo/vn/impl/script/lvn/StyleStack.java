package nl.weeaboo.vn.impl.script.lvn;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;

final class StyleStack {

    private final List<TaggedEntry> stack;
    private transient TextStyle calculated;

    public StyleStack() {
        stack = new ArrayList<>();
    }

    public void clear() {
        if (!stack.isEmpty()) {
            stack.clear();
            onStackChanged();
        }
    }

    public void pushWithTag(String tag, TextStyle style) {
        Checks.checkNotNull(style, "style");

        stack.add(new TaggedEntry(tag, style));
        onStackChanged();
    }

    public boolean popWithTag(String tag) {
        ListIterator<TaggedEntry> litr = stack.listIterator(stack.size());
        while (litr.hasPrevious()) {
            TaggedEntry entry  = litr.previous();
            if (tag.equals(entry.tag)) {
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

    private static class TaggedEntry {

        final String tag;
        final TextStyle style;

        public TaggedEntry(String tag, TextStyle style) {
            this.tag = Checks.checkNotNull(tag);
            this.style = Checks.checkNotNull(style);
        }

    }

}
