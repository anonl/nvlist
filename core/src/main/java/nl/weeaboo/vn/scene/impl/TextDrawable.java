package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.text.ITextRenderer;
import nl.weeaboo.vn.text.impl.TextRenderer;

public class TextDrawable extends Transformable implements ITextDrawable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final ITextRenderer textRenderer = new TextRenderer();

    public TextDrawable() {
    }

    @Override
    public void onTick() {
        increaseVisibleText(.5f);

        textRenderer.update();
    }

    @Override
    protected double getUnscaledWidth() {
        return textRenderer.getMaxWidth();
    }

    @Override
    protected double getUnscaledHeight() {
        return textRenderer.getMaxHeight();
    }

    @Override
    public void setUnscaledSize(double w, double h) {
        // Copy textdrawable size to embedded textrenderer
        setMaxSize((float)w, (float)h);
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        textRenderer.render(this, getAlignOffsetX(), getAlignOffsetY(), drawBuffer);
    }

    @Override
    public int getStartLine() {
        return textRenderer.getStartLine();
    }

    @Override
    public int getEndLine() {
        return textRenderer.getEndLine();
    }

    @Override
    public int[] getHitTags(float cx, float cy) {
        return textRenderer.getHitTags(cx, cy);
    }

    @Override
    public StyledText getText() {
        return textRenderer.getText();
    }

    @Override
    public void setText(String text) {
        textRenderer.setText(text);
    }

    @Override
    public int getLineCount() {
        return textRenderer.getLineCount();
    }

    @Override
    public void increaseVisibleText(float textSpeed) {
        textRenderer.increaseVisibleText(textSpeed);
    }

    @Override
    public void setText(StyledText stext) {
        textRenderer.setText(stext);
    }

    @Override
    public float getVisibleText() {
        return textRenderer.getVisibleText();
    }

    @Override
    public int getGlyphOffset(int line) {
        return textRenderer.getGlyphOffset(line);
    }

    @Override
    public TextStyle getDefaultStyle() {
        return textRenderer.getDefaultStyle();
    }

    @Override
    public void setVisibleText(float visibleGlyphs) {
        textRenderer.setVisibleText(visibleGlyphs);
    }

    @Override
    public float getMaxWidth() {
        return textRenderer.getMaxWidth();
    }

    @Override
    public void setDefaultStyle(TextStyle style) {
        textRenderer.setDefaultStyle(style);
    }

    @Override
    public float getMaxHeight() {
        return textRenderer.getMaxHeight();
    }

    @Override
    public boolean isRightToLeft() {
        return textRenderer.isRightToLeft();
    }

    @Override
    public void setVisibleText(int startLine, float visibleGlyphs) {
        textRenderer.setVisibleText(startLine, visibleGlyphs);
    }

    @Override
    public ITextLayout getVisibleLayout() {
        return textRenderer.getVisibleLayout();
    }

    @Override
    public void setRightToLeft(boolean rtl) {
        textRenderer.setRightToLeft(rtl);
    }

    @Override
    public void setMaxSize(float w, float h) {
        textRenderer.setMaxSize(w, h);
    }

    @Override
    public float getTextWidth() {
        return textRenderer.getTextWidth();
    }

    @Override
    public float getTextHeight() {
        return textRenderer.getTextHeight();
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        return textRenderer.getTextHeight(startLine, endLine);
    }

}
