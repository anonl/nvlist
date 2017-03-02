package nl.weeaboo.vn.impl.scene;

import static nl.weeaboo.vn.impl.text.TextUtil.toStyledText;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.IShape;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Polygon;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IButtonModel;
import nl.weeaboo.vn.scene.IButtonRenderer;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptFunction;

public class Button extends Transformable implements IButton {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final IScriptEventDispatcher eventDispatcher;
    private final IButtonModel model;
    private final IButtonRenderer renderer;

    private double touchMargin;
    private double alphaEnableThreshold = 0.9;
    private IScriptFunction clickHandler;

    private transient IEventListener rendererListener;

    public Button(IScriptEventDispatcher eventDispatcher) {
        this(eventDispatcher, new ButtonModel(), new ButtonRenderer());
    }

    public Button(IScriptEventDispatcher eventDispatcher, IButtonModel model, IButtonRenderer renderer) {
        this.eventDispatcher = Checks.checkNotNull(eventDispatcher);
        this.model = Checks.checkNotNull(model);
        this.renderer = Checks.checkNotNull(renderer);

        initTransients();
    }

    private void initTransients() {
        rendererListener = new IEventListener() {
            @Override
            public void onEvent() {
                invalidateTransform();
            }
        };

        renderer.onAttached(rendererListener);
    }

    @Override
    protected void onDestroyed() {
        super.onDestroyed();

        renderer.onDetached(rendererListener);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (!isEnabled()) {
            renderer.setViewState(ButtonViewState.DISABLED);
        } else if (isPressed() || isSelected()) {
            renderer.setViewState(ButtonViewState.PRESSED);
        } else if (isRollover()) {
            renderer.setViewState(ButtonViewState.ROLLOVER);
        } else {
            renderer.setViewState(ButtonViewState.DEFAULT);
        }

        renderer.update();
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
        super.handleInput(parentTransform, input);

        Vec2 pointerPos = input.getPointerPos(parentTransform);

        boolean enabled = isEnabled() && isVisible(alphaEnableThreshold);
        boolean mouseContains = enabled && contains(pointerPos.x, pointerPos.y);

        if (enabled) {
            model.setRollover(mouseContains);
            if (mouseContains && input.consumePress(VKey.MOUSE_LEFT)) {
                model.setPressed(true);
            }
            if (!input.isPressed(VKey.MOUSE_LEFT, true)) {
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
        renderer.render(drawBuffer, this, getAlignOffsetX(), getAlignOffsetY());
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
        return renderer.getText();
    }

    @Override
    public void setText(String s) {
        setText(toStyledText(s));
    }

    @Override
    public void setText(StyledText stext) {
        renderer.setText(stext);
    }

    @Override
    protected double getUnscaledWidth() {
        return renderer.getWidth();
    }

    @Override
    protected double getUnscaledHeight() {
        return renderer.getHeight();
    }

    @Override
    public void setUnscaledSize(double w, double h) {
        renderer.setSize(w, h);

        invalidateTransform();
    }

    @Override
    public void setTexture(ButtonViewState viewState, ITexture tex) {
        renderer.setTexture(viewState, tex);
    }

    @Override
    public void setTexture(ButtonViewState viewState, INinePatch patch) {
        renderer.setTexture(viewState, patch);
    }

    @Override
    public VerticalAlign getVerticalAlign() {
        return renderer.getVerticalAlign();
    }

    @Override
    public void setVerticalAlign(VerticalAlign align) {
        renderer.setVerticalAlign(align);
    }

}
