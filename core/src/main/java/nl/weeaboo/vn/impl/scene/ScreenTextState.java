package nl.weeaboo.vn.impl.scene;

import static nl.weeaboo.vn.impl.text.TextUtil.toStyledText;

import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.scene.IScreenTextState;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.text.ITextLog;

public class ScreenTextState implements IScreenTextState {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private double baseTextSpeed;
    private StyledText stext = toStyledText("");
    private ITextLog textLog;
    private ITextDrawable textDrawable;

    public ScreenTextState(ITextLog textLog) {
        this.textLog = Checks.checkNotNull(textLog);

        baseTextSpeed = NovelPrefs.TEXT_SPEED.getDefaultValue();
    }

    @Override
    public void update() {
    }

    protected void onTextSpeedChanged() {
        if (textDrawable != null) {
            textDrawable.setTextSpeed(getTextSpeed());
        }
    }

    @Override
    public ITextLog getTextLog() {
        return textLog;
    }

    @Override
    public ITextDrawable getTextDrawable() {
        return textDrawable;
    }

    @Override
    public double getTextSpeed() {
        return baseTextSpeed;
    }

    @Override
    public StyledText getText() {
        return stext;
    }

    @Override
    public void setText(String s) {
        setText(toStyledText(s));
    }

    @Override
    public void setText(StyledText st) {
        stext = st;

        if (textDrawable != null) {
            textDrawable.setVisibleText(0, 0);
        }
        setTextDrawableText(st);
    }

    @Override
    public void appendText(String s) {
        appendText(toStyledText(s));
    }

    @Override
    public void appendText(StyledText st) {
        stext = StyledText.concat(stext, st);
        setTextDrawableText(stext);
    }

    private void setTextDrawableText(StyledText stext) {
        if (textDrawable == null) {
            return;
        }

        int sl = textDrawable.getStartLine();
        double visible = textDrawable.getVisibleText();
        int maxVisible = textDrawable.getMaxVisibleText();
        if (visible < 0 || visible >= maxVisible) {
            visible = maxVisible;
        }

        textDrawable.setText(stext);
        textDrawable.setVisibleText(sl, visible);
    }

    @Override
    public void setTextDrawable(ITextDrawable td) {
        if (textDrawable != td) {
            textDrawable = td;

            if (textDrawable != null) {
                textDrawable.setText(stext);
                textDrawable.setTextSpeed(getTextSpeed());
            }
        }
    }

    @Override
    public void setTextSpeed(double ts) {
        if (baseTextSpeed != ts) {
            baseTextSpeed = ts;
            onTextSpeedChanged();
        }
    }

}
