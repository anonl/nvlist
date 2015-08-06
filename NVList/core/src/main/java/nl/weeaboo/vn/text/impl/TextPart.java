package nl.weeaboo.vn.text.impl;

import com.google.common.base.Objects;

import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.LayoutUtil;
import nl.weeaboo.vn.core.impl.DrawablePart;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.render.impl.DrawBuffer;
import nl.weeaboo.vn.text.ITextPart;

public class TextPart extends DrawablePart implements ITextPart {

    private static final long serialVersionUID = TextImpl.serialVersionUID;
    private static final float ALL_GLYPHS_VISIBLE = 999999;

    private final StaticRef<IFontStore> fontStore = StaticEnvironment.FONT_STORE;

    private StyledText stext = StyledText.EMPTY_STRING;
    private TextStyle defaultStyle = DEFAULT_STYLE;

    private float visibleGlyphs = ALL_GLYPHS_VISIBLE;
    private boolean rightToLeft;

    private transient ITextLayout layout;

    protected ITextLayout getLayout() {
        if (layout == null) {
            layout = createLayout(getLayoutMaxWidth());
        }
        return layout;
    }

    protected ITextLayout createLayout(int wrapWidth) {
        MutableStyledText newText = getText().mutableCopy();
        newText.setBaseStyle(getDefaultStyle());
        StyledText stext = newText.immutableCopy();

        LayoutParameters layoutParams = new LayoutParameters();
        layoutParams.wrapWidth = wrapWidth;
        layoutParams.isRightToLeft = rightToLeft;
        return LayoutUtil.layout(fontStore.get(), stext, layoutParams);
    }

    @Override
    protected void invalidateTransform() {
        super.invalidateTransform();

        invalidateLayout();
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

    protected void invalidateLayout() {
        layout = null;

        markChanged();
    }

    protected void onVisibleTextChanged() {
        markChanged();
    }

    @Override
    public float increaseVisibleText(float textSpeed) {
        visibleGlyphs = LayoutUtil.increaseVisibleCharacters(getLayout(), visibleGlyphs, textSpeed);
        onVisibleTextChanged();
        return textSpeed;
    }

    protected float getVisibleGlyphs() {
        return visibleGlyphs;
    }

    protected float getCursorWidth() {
        return 0;
    }

    protected int getLayoutMaxWidth() {
        return (int)Math.ceil(getWidth() - getCursorWidth());
    }

    protected int getLayoutMaxHeight() {
        return (int)Math.ceil(getHeight());
    }

    @Override
    public float getTextWidth() {
        return getLayout().getTextWidth();
    }

    @Override
    public float getTextHeight() {
        return getLayout().getTextHeight();
    }

    @Override
    public boolean isRightToLeft() {
        return rightToLeft;
    }

    protected double getPadLeft() {
        return (rightToLeft ? getCursorWidth() : 0);
    }

    @Override
    public void setVisibleText(float vc) {
        if (visibleGlyphs != vc) {
            visibleGlyphs = vc;

            onVisibleTextChanged();
        }
    }

    @Override
    public void setRightToLeft(boolean rtl) {
        if (rightToLeft != rtl) {
            rightToLeft = rtl;

            invalidateLayout();
        }
    }

    public void draw(DrawBuffer drawBuffer) {
        ITextLayout textLayout = getLayout();
        float x = Math.round(getX());
        float y = Math.round(getY() + getHeight());
        
        drawBuffer.drawText(getZ(), isClipEnabled(), getBlendMode(),
                textLayout, visibleGlyphs, x, y);
    }

}
