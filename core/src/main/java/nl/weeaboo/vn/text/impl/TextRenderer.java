package nl.weeaboo.vn.text.impl;

import static nl.weeaboo.vn.text.impl.TextUtil.toStyledText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.impl.AbstractRenderable;
import nl.weeaboo.vn.text.ITextRenderer;

public class TextRenderer extends AbstractRenderable implements ITextRenderer {

    private static final long serialVersionUID = TextImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(TextRenderer.class);

    private final StaticRef<IFontStore> fontStore = StaticEnvironment.FONT_STORE;

    private StyledText stext = StyledText.EMPTY_STRING;
    private TextStyle defaultStyle = DEFAULT_STYLE;
    private double visibleGlyphs = ALL_GLYPHS_VISIBLE;
    private boolean rightToLeft;

    private int startLine;
    private double maxWidth = -1;
    private double maxHeight = -1;

    private transient ITextLayout _layout;
    private transient ITextLayout _visibleLayout;

    protected ITextLayout createLayout(int wrapWidth) {
        MutableStyledText newText = getText().mutableCopy();
        newText.setBaseStyle(getDefaultStyle());
        StyledText stext = newText.immutableCopy();

        LayoutParameters layoutParams = new LayoutParameters();
        layoutParams.wrapWidth = wrapWidth;
        layoutParams.isRightToLeft = isRightToLeft();
        return LayoutUtil.layout(fontStore.get(), stext, layoutParams);
    }

    @Override
    public void render(IDrawBuffer buffer, IDrawable d, Area2D bounds) {
        double visibleText = getVisibleText();
        if (visibleText == 0) {
            return;
        }

        ITextLayout textLayout = getVisibleLayout();
        double dx = bounds.x;
        double dy = bounds.y + getHeight();
        buffer.drawText(d, dx, dy, textLayout, visibleText);
    }

    protected final ITextLayout getLayout() {
        if (_layout == null) {
            _layout = createLayout(getLayoutMaxWidth());
        }
        return _layout;
    }

    protected void invalidateLayout() {
        _layout = null;
        _visibleLayout = null;
    }

    @Override
    public final ITextLayout getVisibleLayout() {
        if (_visibleLayout == null) {
            ITextLayout layout = getLayout();
            int count = LayoutUtil.getVisibleLines(layout, startLine, getLayoutMaxHeight());
            int endLine = Math.min(layout.getLineCount(), startLine + count);
            _visibleLayout = layout.getLineRange(startLine, endLine);

            LOG.debug("Text layout created: startLine={}, endLine={}, height={}/{}",
                    startLine, endLine, _visibleLayout.getTextHeight(), getLayoutMaxHeight());
        }
        return _visibleLayout;
    }

    protected void onVisibleTextChanged() {
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

    protected float getCursorWidth() {
        return 0f;
    }

    protected int getLayoutMaxWidth() {
        return (int)Math.floor(getMaxWidth() - getCursorWidth());
    }

    protected int getLayoutMaxHeight() {
        return (int)Math.floor(getMaxHeight());
    }

    @Override
    public final float getTextWidth() {
        return getVisibleLayout().getTextWidth();
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
        if (!Objects.equal(stext, st)) {
            stext = st;

            invalidateLayout();
        }

        setVisibleText(ALL_GLYPHS_VISIBLE);
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
        if (textSpeed == 0) {
            return; // Nothing to do
        }

        setVisibleText(LayoutUtil.increaseVisibleCharacters(getVisibleLayout(),
                (float)visibleGlyphs, (float)textSpeed));
    }

    @Override
    public double getVisibleText() {
        return visibleGlyphs;
    }

    @Override
    public int getMaxVisibleText() {
        return getVisibleLayout().getGlyphCount();
    }

    @Override
    public void setVisibleText(double vc) {
        // Limit value to its maximum
        vc = Math.min(vc, getMaxVisibleText());

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

    /** @return The vertical offset for aligning content within its parent bounds */
    public static double getOffsetY(ITextRenderer textRenderer, VerticalAlign verticalAlign) {
        double boundingHeight = textRenderer.getHeight();
        double contentHeight = textRenderer.getTextHeight();

        switch (verticalAlign) {
        case TOP:
            return 0;
        case MIDDLE:
            return (contentHeight - boundingHeight) / 2;
        case BOTTOM:
            return (contentHeight - boundingHeight);
        default:
            throw new IllegalArgumentException("Unsupported alignment: " + verticalAlign);
        }
    }

    @Override
    public boolean isFinalLineFullyVisible() {
        return getEndLine() >= getLineCount() && getVisibleText() >= getMaxVisibleText();
    }

}
