package nl.weeaboo.vn.impl.text;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.text.ITextRenderer;

public class MockTextRenderer implements ITextRenderer {

    private static final long serialVersionUID = 1L;

    private double width = 100;
    private double height = 20;

    private int startLine;
    private int endLine = 2;
    private double visibleText = 5;
    private int maxVisibleText = 10;

    private boolean rightToLeft;

    private StyledText text = new StyledText("abcdefghij");
    private TextStyle defaultStyle = TextStyle.defaultInstance();

    @Override
    public @Nullable int[] getHitTags(double cx, double cy) {
        return null;
    }

    @Override
    public void increaseVisibleText(double textSpeed) {
    }

    @Override
    public double getVisibleText() {
        return visibleText;
    }

    @Override
    public int getMaxVisibleText() {
        return maxVisibleText;
    }

    @Override
    public boolean isFinalLineFullyVisible() {
        return false;
    }

    @Override
    public void setVisibleText(double visibleGlyphs) {

    }

    @Override
    public void setVisibleText(int startLine, double visibleGlyphs) {
    }

    @Override
    public ITextLayout getVisibleLayout() {
        return new TextLayoutStub();
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getLineCount() {
        return endLine - startLine;
    }

    @Override
    public int getGlyphOffset(int line) {
        return 0;
    }

    @Override
    public double getMaxWidth() {
        return width;
    }

    @Override
    public double getMaxHeight() {
        return height;
    }

    @Override
    public void setMaxSize(double w, double h) {
        width = w;
        height = h;
    }

    @Override
    public float getTextWidth() {
        return (float)width;
    }

    @Override
    public float getTextHeight() {
        return (float)height;
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        return getTextHeight();
    }

    @Override
    public StyledText getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        setText(new StyledText(text));
    }

    @Override
    public void setText(StyledText stext) {
        this.text = Checks.checkNotNull(stext);
    }

    @Override
    public TextStyle getDefaultStyle() {
        return defaultStyle;
    }

    @Override
    public void setDefaultStyle(TextStyle style) {
        this.defaultStyle = style;
    }

    @Override
    public void extendDefaultStyle(TextStyle ts) {
        setDefaultStyle(defaultStyle.extend(ts));
    }

    @Override
    public boolean isRightToLeft() {
        return rightToLeft;
    }

    @Override
    public void setRightToLeft(boolean rtl) {
        rightToLeft = rtl;
    }

    @Override
    public void onAttached(IEventListener cl) {

    }

    @Override
    public void onDetached(IEventListener cl) {

    }

    @Override
    public double getNativeWidth() {
        return width;
    }

    @Override
    public double getNativeHeight() {
        return height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double w, double h) {
        width = w;
        height = h;
    }

    @Override
    public Rect2D getVisualBounds() {
        return Rect2D.EMPTY;
    }

    @Override
    public Rect2D getLineBounds(int lineIndex) {
        return Rect2D.EMPTY;
    }

    @Override
    public void render(IDrawBuffer drawBuffer, IDrawable parentComponent, double dx, double dy) {
    }

    @Override
    public void update() {
    }

    @Override
    public double calculateTextHeight(double widthHint) {
        return height;
    }

}
