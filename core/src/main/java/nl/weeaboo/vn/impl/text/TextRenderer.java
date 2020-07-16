package nl.weeaboo.vn.impl.text;

import static nl.weeaboo.vn.impl.text.TextUtil.toStyledText;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextElement;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.impl.scene.AbstractRenderable;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.text.ILoadingFontStore;
import nl.weeaboo.vn.text.ITextRenderer;

/**
 * Default implementation of {@link ITextRenderer}.
 */
public final class TextRenderer extends AbstractRenderable implements ITextRenderer {

    private static final long serialVersionUID = TextImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(TextRenderer.class);

    private final ILoadingFontStore fontStore;

    private StyledText stext = StyledText.EMPTY_STRING;
    private TextStyle defaultStyle;
    private double visibleGlyphs = ALL_GLYPHS_VISIBLE;
    private boolean rightToLeft;

    private int startLine;
    private double maxWidth = -1;
    private double maxHeight = -1;

    private transient @Nullable ITextLayout cachedLayout;
    private transient @Nullable ITextLayout cachedVisibleLayout;

    public TextRenderer(ILoadingFontStore fontStore) {
        this.fontStore = Checks.checkNotNull(fontStore);
        this.defaultStyle = fontStore.getDefaultStyle();
    }

    private ITextLayout createLayout(int wrapWidth) {
        MutableStyledText newText = getText().mutableCopy();
        newText.setBaseStyle(getDefaultStyle());

        LayoutParameters layoutParams = new LayoutParameters();
        layoutParams.ydir = 1;
        layoutParams.wrapWidth = wrapWidth;
        layoutParams.isRightToLeft = isRightToLeft();
        return LayoutUtil.layout(fontStore, newText.immutableCopy(), layoutParams);
    }

    @Override
    public void render(IDrawBuffer buffer, IDrawable d, Area2D bounds) {
        double visibleText = getVisibleText();
        if (visibleText == 0) {
            return;
        }

        ITextLayout textLayout = getVisibleLayout();
        double dx = bounds.x;
        /*
         * TODO: Workaround for what appears to be a bug. Internally in the text rendering code, the y-offset
         * isn't flipped if ydir == 1.
         */
        double dy = bounds.y - 2 * textLayout.getOffsetY();
        buffer.drawText(d, dx, dy, textLayout, visibleText);
    }

    private final ITextLayout getLayout() {
        ITextLayout result = cachedLayout;
        if (result == null) {
            result = createLayout(getLayoutMaxWidth());
            cachedLayout = result;
        }
        return result;
    }

    private void invalidateLayout() {
        cachedLayout = null;
        cachedVisibleLayout = null;
    }

    @Override
    public final ITextLayout getVisibleLayout() {
        ITextLayout result = cachedVisibleLayout;
        if (result == null) {
            ITextLayout layout = getLayout();
            int count = LayoutUtil.getVisibleLines(layout, startLine, getLayoutMaxHeight());
            int endLine = Math.min(layout.getLineCount(), startLine + count);
            result = layout.getLineRange(startLine, endLine);

            LOG.trace("Text layout created: lines={}-{}/{}, glyphs={}/{}, height={}/{}",
                    startLine, endLine, getLineCount(),
                    result.getGlyphCount(), layout.getGlyphCount(),
                    result.getTextHeight(), getLayoutMaxHeight());
            cachedVisibleLayout = result;
        }
        return result;
    }

    private void onVisibleTextChanged() {
    }

    @Override
    public final int getStartLine() {
        return startLine;
    }

    @Override
    public int getEndLine() {
        return startLine + getVisibleLayout().getLineCount();
    }

    @Override
    public int getLineCount() {
        return getLayout().getLineCount();
    }

    @Override
    public int getGlyphOffset(int line) {
        return getLayout().getGlyphOffset(line);
    }

    @Override
    public double getNativeWidth() {
        return getTextWidth();
    }

    @Override
    public double getNativeHeight() {
        return getTextHeight();
    }

    @Override
    public final double getMaxWidth() {
        return maxWidth;
    }

    @Override
    public final double getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setMaxSize(double w, double h) {
        if (maxWidth != w || maxHeight != h) {
            maxWidth = w;
            maxHeight = h;

            invalidateLayout();
        }
    }

    private int getLayoutMaxWidth() {
        return (int)Math.floor(getMaxWidth());
    }

    private int getLayoutMaxHeight() {
        return (int)Math.floor(getMaxHeight());
    }

    @Override
    public final float getTextWidth() {
        return getVisibleLayout().getTextWidth();
    }

    @Override
    public Rect2D getLineBounds(int lineIndex) {
        ITextLayout tl = getLayout();
        if (lineIndex < 0 || lineIndex >= tl.getLineCount()) {
            return Rect2D.EMPTY;
        }
        return calculateLineBounds(tl, lineIndex);
    }

    // TODO: Move to styledtext library
    private static Rect2D calculateLineBounds(ITextLayout layout, int lineIndex) {
        ITextLayout lineLayout = layout.getLineRange(lineIndex, lineIndex + 1);

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for (ITextElement elem : lineLayout.getElements()) {
            float x0 = elem.getX();
            float x1 = elem.getX() + elem.getLayoutWidth();
            minX = Math.min(minX, x0);
            minX = Math.min(minX, x1);
            maxX = Math.max(maxX, x0);
            maxX = Math.max(maxX, x1);

            float y0 = elem.getY();
            float y1 = elem.getY() + elem.getLayoutHeight();
            minY = Math.min(minY, y0);
            minY = Math.min(minY, y1);
            maxY = Math.max(maxY, y0);
            maxY = Math.max(maxY, y1);
        }

        if (Float.isInfinite(minX) || Float.isInfinite(maxX) || Float.isInfinite(minY) || Float.isInfinite(maxY)) {
            return Rect2D.EMPTY;
        }
        return Rect2D.of(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public final float getTextHeight() {
        return getVisibleLayout().getTextHeight();
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        ITextLayout tl = getLayout();
        return tl.getTextHeight(startLine, endLine);
    }

    @Override
    public int[] getHitTags(double cx, double cy) {
        // TODO: Implement
        return new int[0];
    }

    @Override
    public StyledText getText() {
        return stext;
    }

    @Override
    public TextStyle getDefaultStyle() {
        return defaultStyle;
    }

    @Override
    public void setText(String s) {
        setText(toStyledText(s));
    }

    @Override
    public void setText(StyledText st) {
        Checks.checkNotNull(st);

        if (!Objects.equal(stext, st)) {
            stext = st;

            invalidateLayout();
        }

        setVisibleText(ALL_GLYPHS_VISIBLE);
    }

    @Override
    public void extendDefaultStyle(TextStyle ts) {
        setDefaultStyle(getDefaultStyle().extend(ts));
    }

    @Override
    public void setDefaultStyle(TextStyle ts) {
        Checks.checkNotNull(ts);

        if (!Objects.equal(defaultStyle, ts)) {
            defaultStyle = ts;

            invalidateLayout();
        }
    }

    @Override
    public void increaseVisibleText(double textSpeed) {
        if (textSpeed <= 0) {
            return; // Nothing to do
        }

        setVisibleText(LayoutUtil.increaseVisibleCharacters(getVisibleLayout(),
                (float)visibleGlyphs, (float)textSpeed));
    }

    @Override
    public double getVisibleText() {
        return Math.min(visibleGlyphs, getMaxVisibleText());
    }

    @Override
    public int getMaxVisibleText() {
        return getVisibleLayout().getGlyphCount();
    }

    @Override
    public void setVisibleText(double vc) {
        /*
         * Fix for #46: Remember the intention to show all glyphs, so the visible glyphs aren't reduced by changes in
         * the visible layout.
         */
        vc = Math.min(vc, ALL_GLYPHS_VISIBLE);

        if (visibleGlyphs != vc) {
            visibleGlyphs = vc;

            onVisibleTextChanged();
        }
    }

    @Override
    public void setVisibleText(int sl, double vc) {
        if (startLine != sl) {
            startLine = sl;

            invalidateLayout();
        }

        setVisibleText(vc);
    }

    @Override
    public final boolean isRightToLeft() {
        return rightToLeft;
    }

    @Override
    public void setRightToLeft(boolean rtl) {
        if (rightToLeft != rtl) {
            rightToLeft = rtl;

            invalidateLayout();
        }
    }

    @Override
    public void setSize(double w, double h) {
        setMaxSize((int)Math.floor(w), (int)Math.floor(h));

        super.setSize(w, h);
    }

    /**
     * @return The vertical offset for aligning content within its parent bounds.
     */
    public static double getOffsetY(ITextRenderer textRenderer, VerticalAlign verticalAlign) {
        double boundingHeight = textRenderer.getHeight();
        double contentHeight = textRenderer.getTextHeight();

        switch (verticalAlign) {
        case TOP:
            return 0;
        case MIDDLE:
            return (boundingHeight - contentHeight) / 2;
        case BOTTOM:
            return (boundingHeight - contentHeight);
        }

        throw new IllegalArgumentException("Unsupported alignment: " + verticalAlign);
    }

    @Override
    public boolean isFinalLineFullyVisible() {
        return getEndLine() >= getLineCount() && getVisibleText() >= getMaxVisibleText();
    }

    @Override
    public double calculateTextHeight(double widthHint) {
        ITextLayout layout = createLayout((int)Math.ceil(widthHint));
        return layout.getTextHeight();
    }
}
