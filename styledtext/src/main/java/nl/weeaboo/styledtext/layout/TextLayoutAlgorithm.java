package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.RunSplitter.RunHandler;
import nl.weeaboo.styledtext.layout.RunSplitter.RunState;

final class TextLayoutAlgorithm implements RunHandler {

    private final IFontStore fontStore;

    private LayoutParameters params;
    private final List<LineLayout> finishedLines = new ArrayList<LineLayout>();
    private LineLayout currentLine;

    public TextLayoutAlgorithm(IFontStore fontStore) {
        this.fontStore = fontStore;
    }

    private void init(LayoutParameters params) {
        this.params = params;

        finishedLines.clear();
        currentLine = new LineLayout(params);
    }

    public ITextLayout layout(StyledText stext, LayoutParameters params) {
        init(params);

        RunSplitter.run(stext, params.isRightToLeft, this);
        endLine();

        float y = 0f;
        CompositeTextLayout compositeLayout = new CompositeTextLayout();
        for (LineLayout line : finishedLines) {
            compositeLayout.add(line.layout(0f, y));
            y -= line.getLayoutHeight();
        }
        return compositeLayout;
    }

    @Override
    public void processRun(CharSequence text, RunState rs) {
        TextStyle style = rs.style;
        if (style == null) {
            style = TextStyle.defaultInstance();
        }

        IFontMetrics metrics = fontStore.getFontMetrics(style);
        if (metrics == null) {
            return; // Font not found
        }

        ILayoutElement elem;
        if (rs.isWhitespace) {
            elem = metrics.layoutSpacing(text, style, params);
        } else {
            elem = metrics.layoutText(text, style, params);
        }

        if (currentLine.fits(elem)) {
            currentLine.add(elem);
        } else {
            if (elem.isWhitespace()) {
                // Allow whitespace to overflow wrap width
                currentLine.add(elem);
                endLine();
            } else {
                endLine();
                currentLine.add(elem);
            }
        }

        if (rs.containsLineBreak) {
            endLine();
        }
    }

    private void endLine() {
        if (currentLine != null && !currentLine.isEmpty()) {
            finishedLines.add(currentLine);
            currentLine = new LineLayout(params);
        }
    }

    private static class LineLayout {

        private final LayoutParameters params;
        private final List<ILayoutElement> elements = new ArrayList<ILayoutElement>();

        private float layoutWidth;
        private float layoutHeight;

        public LineLayout(LayoutParameters params) {
            this.params = params;
        }

        public void add(ILayoutElement elem) {
            elements.add(elem);
            layoutWidth += elem.getLayoutWidth();
        }

        public boolean fits(ILayoutElement elem) {
            return elem.isWhitespace()
                || params.wrapWidth < 0
                || layoutWidth + elem.getLayoutWidth() <= params.wrapWidth;
        }

        public List<ILayoutElement> layout(float x, float y) {
            removeTrailingWhitespace();

            final List<SpacingElement> paddingElements = new ArrayList<SpacingElement>();
            final List<ILayoutElement> newElements = new ArrayList<ILayoutElement>(elements.size());

            addPadding(newElements, paddingElements);

            // Assign widths to the spacing elements
            float freeSpace = (params.wrapWidth >= 0 ? params.wrapWidth - layoutWidth : 0);
            if (!paddingElements.isEmpty() && freeSpace > 0) {
                final float gapSize = freeSpace / paddingElements.size();
                for (SpacingElement padding : paddingElements) {
                    float w = Math.min(freeSpace, gapSize);
                    padding.setLayoutWidth(w);
                    freeSpace -= w;
                }
            }

            // TODO Sort newElements in visual order

            // Position text elements and calculate line width/height
            layoutWidth = 0f;
            layoutHeight = 0f;
            for (ILayoutElement elem : newElements) {
                if (elem instanceof TextElement) {
                    TextElement text = (TextElement)elem;
                    text.setX(x);
                    text.setY(y);
                }

                x += elem.getLayoutWidth();
                layoutWidth += elem.getLayoutWidth();
                layoutHeight = Math.max(layoutHeight, elem.getLayoutHeight());
            }

            return newElements;
        }

        /** Add padding wherever the horizontal layout switches */
        private void addPadding(List<ILayoutElement> outElements, List<SpacingElement> outSpacing) {
            final boolean isRTL = params.isRightToLeft;
            final int defaultHAlign = (isRTL ? 1 : -1);

            int halign = defaultHAlign;
            for (ILayoutElement elem : elements) {
                if (elem instanceof TextElement) {
                    TextElement textElem = (TextElement)elem;
                    int halign2 = textElem.getAlign().getHorizontalAlign(isRTL);
                    if (halign == 1 && halign2 == -1) {
                        // Don't add padding between elements that want to touch
                    } else if (halign != halign2) {
                        SpacingElement padding = new SpacingElement();
                        outElements.add(padding);
                        outSpacing.add(padding);
                        halign = halign2;
                    }
                }

                outElements.add(elem);
            }
            if (halign != -defaultHAlign) {
                SpacingElement padding = new SpacingElement();
                outElements.add(padding);
                outSpacing.add(padding);
            }
        }

        public float getLayoutHeight() {
            return layoutHeight;
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }

        private void removeTrailingWhitespace() {
            ListIterator<ILayoutElement> litr = elements.listIterator(elements.size());
            while (litr.hasPrevious()) {
                ILayoutElement elem = litr.previous();
                if (!elem.isWhitespace()) {
                    break;
                }

                litr.remove();
                layoutWidth -= elem.getLayoutWidth();
            }
        }

    }

}
