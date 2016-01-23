package nl.weeaboo.vn.text.impl;

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
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.impl.AbstractRenderable;
import nl.weeaboo.vn.text.ITextRenderer;

public class TextRenderer extends AbstractRenderable implements ITextRenderer {

    private static final long serialVersionUID = TextImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(TextRenderer.class);
    private static final float ALL_GLYPHS_VISIBLE = 999999;

    private final StaticRef<IFontStore> fontStore = StaticEnvironment.FONT_STORE;

    private StyledText stext = StyledText.EMPTY_STRING;
    private TextStyle defaultStyle = DEFAULT_STYLE;
    private float visibleGlyphs = ALL_GLYPHS_VISIBLE;
    private boolean rightToLeft;

    private int startLine;
    private float maxWidth = -1f;
    private float maxHeight = -1f;

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
    public void render(IDrawable d, Area2D bounds, IDrawBuffer buffer) {
        float visibleText = getVisibleText();
        if (visibleText == 0f) {
            return;
        }

        ITextLayout textLayout = getVisibleLayout();
        buffer.drawText(d, bounds.x, bounds.y + getTextHeight(), textLayout, visibleText);
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
    public final float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public final float getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setMaxSize(float w, float h) {
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
    public int[] getHitTags(float cx, float cy) {
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
        setText(new StyledText(s != null ? s : ""));
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
    public void increaseVisibleText(float textSpeed) {
        setVisibleText(LayoutUtil.increaseVisibleCharacters(getVisibleLayout(), visibleGlyphs, textSpeed));
    }

    @Override
    public float getVisibleText() {
        return visibleGlyphs;
    }

    @Override
    public void setVisibleText(float vc) {
        if (visibleGlyphs != vc) {
            visibleGlyphs = vc;

            onVisibleTextChanged();
        }
    }

    @Override
    public void setVisibleText(int sl, float vc) {
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

}
