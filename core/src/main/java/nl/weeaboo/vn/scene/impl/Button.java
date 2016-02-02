package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.KeyCode;
import nl.weeaboo.vn.image.impl.NinePatchRenderer;
import nl.weeaboo.vn.math.IShape;
import nl.weeaboo.vn.math.Polygon;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IButtonModel;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.text.impl.TextRenderer;

public class Button extends Transformable implements IButton {

    // TODO: Store all view state in a ButtonSkin or ButtonRenderer class
    // - The button class acts as a controller

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final IButtonModel model = new ButtonModel();
    private final NinePatchRenderer ninePatch = new NinePatchRenderer();
    private final TextRenderer textRenderer = new TextRenderer();
    private final IScriptEventDispatcher eventDispatcher;

    private double touchMargin;
    private double alphaEnableThreshold = 0.9;
    private IScriptFunction clickHandler;

    private transient IEventListener rendererListener;

    public Button(IScriptEventDispatcher eventDispatcher) {
        this.eventDispatcher = Checks.checkNotNull(eventDispatcher);

        initTransients();
    }

    private void initTransients() {
        rendererListener = new IEventListener() {
            @Override
            public void onEvent() {
                invalidateTransform();
            }
        };

        ninePatch.onAttached(rendererListener);
        textRenderer.onAttached(rendererListener);
    }

    @Override
    protected void onDestroyed() {
        super.onDestroyed();

        ninePatch.onDetached(rendererListener);
        textRenderer.onDetached(rendererListener);
    }

    @Override
    public void onTick() {
        super.onTick();

        ninePatch.update();
        textRenderer.update();
    }

    @Override
    protected void handleInput(IInput input) {
        super.handleInput(input);

        boolean enabled = isEnabled() && isVisible(alphaEnableThreshold);
        boolean mouseContains = enabled && contains(input.getPointerX(), input.getPointerY());

        if (enabled) {
            model.setRollover(mouseContains);
            if (mouseContains && input.consumePress(KeyCode.MOUSE_LEFT)) {
                model.setPressed(true);
            }
            if (!input.isPressed(KeyCode.MOUSE_LEFT, true)) {
                model.setPressed(false);
            }
        } else {
            model.setRollover(false);
            model.setPressed(false);
        }

        if (clickHandler != null && model.consumePress()) {
            eventDispatcher.addEvent(clickHandler);
        }
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        ninePatch.render(this, 0, 0, drawBuffer);
        textRenderer.render(this, 0, 0, drawBuffer);
    }

    @Override
    protected IShape createCollisionShape() {
        Rect2D r = getUntransformedVisualBounds();

        r = Rect2D.of(
                r.x + getAlignOffsetX() - touchMargin,
                r.y + getAlignOffsetY() - touchMargin,
                r.w + 2 * touchMargin,
                r.h + 2 * touchMargin);

        return Polygon.transformedRect(getTransform(), r);
    }

    @Override
    public boolean consumePress() {
        return model.consumePress();
    }

    @Override
    public boolean isRollover() {
        return model.isRollover();
    }

    @Override
    public boolean isPressed() {
        return model.isPressed();
    }

    @Override
    public boolean isEnabled() {
        return model.isEnabled();
    }

    @Override
    public void setEnabled(boolean e) {
        model.setEnabled(e);
    }

    @Override
    public boolean isSelected() {
        return model.isSelected();
    }

    @Override
    public void setSelected(boolean s) {
        model.setSelected(s);
    }

    @Override
    public boolean isToggle() {
        return model.isToggle();
    }

    @Override
    public void setToggle(boolean t) {
        model.setToggle(t);
    }

    @Override
    public IScriptFunction getClickHandler() {
        return clickHandler;
    }

    @Override
    public void setClickHandler(IScriptFunction func) {
        clickHandler = func;
    }

    @Override
    public double getTouchMargin() {
        return touchMargin;
    }

    @Override
    public void setTouchMargin(double margin) {
        if (touchMargin != margin) {
            touchMargin = margin;

            invalidateCollisionShape();
        }
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
    public void setText(StyledText stext) {
        textRenderer.setText(stext);
    }

    @Override
    protected double getUnscaledWidth() {
        return Math.max(ninePatch.getWidth(), textRenderer.getWidth());
    }

    @Override
    protected double getUnscaledHeight() {
        return Math.max(ninePatch.getHeight(), textRenderer.getHeight());
    }

    @Override
    protected void setUnscaledSize(double w, double h) {
        ninePatch.setSize(w, h);
        textRenderer.setSize(w, h);

        invalidateTransform();
    }

}
