package nl.weeaboo.vn.impl.scene;

import java.util.Map;

import com.google.common.collect.Maps;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.ETextAlign;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.NinePatch;
import nl.weeaboo.vn.impl.image.NinePatchRenderer;
import nl.weeaboo.vn.impl.text.TextRenderer;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButtonRenderer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.text.IText;

public class ButtonRenderer extends AbstractRenderable implements IButtonRenderer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final NinePatchRenderer background = new NinePatchRenderer();
    private final TextRenderer textRenderer = new TextRenderer();

    private final Map<ButtonViewState, INinePatch> textures = Maps.newEnumMap(ButtonViewState.class);

    private ButtonViewState currentViewState = ButtonViewState.DEFAULT;
    private VerticalAlign verticalTextAlign = VerticalAlign.MIDDLE;

    public ButtonRenderer() {
        MutableTextStyle mts = new MutableTextStyle(IText.DEFAULT_STYLE);
        mts.setAlign(ETextAlign.CENTER);
        textRenderer.setDefaultStyle(mts.immutableCopy());

        textures.put(ButtonViewState.DEFAULT, new NinePatch());
    }

    @Override
    public void onAttached(IEventListener listener) {
        background.onAttached(listener);
        textRenderer.onAttached(listener);

        super.onAttached(listener);
    }

    @Override
    public void onDetached(IEventListener listener) {
        background.onDetached(listener);
        textRenderer.onDetached(listener);

        super.onDetached(listener);
    }

    protected void invalidateBackground() {
        INinePatch patch = textures.get(currentViewState);
        if (patch == null) {
            patch = textures.get(ButtonViewState.DEFAULT);
        }

        if (patch != null) {
            background.set(patch);
        }
    }

    @Override
    protected void render(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        background.render(drawBuffer, parent, bounds);

        // TODO: Button text seems to be rendered too far down
        double ty = TextRenderer.getOffsetY(textRenderer, verticalTextAlign);
        Area2D textBounds = Area2D.of(bounds.x, bounds.y + ty, bounds.w, textRenderer.getTextHeight());
        textRenderer.render(drawBuffer, parent, textBounds);
    }

    @Override
    public double getNativeWidth() {
        return Math.max(background.getNativeWidth(), textRenderer.getNativeWidth());
    }

    @Override
    public double getNativeHeight() {
        return Math.max(background.getNativeHeight(), textRenderer.getNativeHeight());
    }

    @Override
    public void setSize(double w, double h) {
        background.setSize(w, h);
        textRenderer.setSize(w, h);

        super.setSize(w, h);
    }

    @Override
    public StyledText getText() {
        return textRenderer.getText();
    }

    @Override

    public void setText(StyledText stext) {
        textRenderer.setText(stext);

        pack();
    }

    @Override
    public void setTexture(ButtonViewState viewState, ITexture tex) {
        setTexture(viewState, new NinePatch(tex));
    }

    @Override
    public void setTexture(ButtonViewState viewState, INinePatch tex) {
        if (tex == null) {
            textures.remove(viewState);
        } else {
            textures.put(viewState, tex);
        }

        invalidateBackground();
        pack();
    }

    @Override
    public ButtonViewState getViewState() {
        return currentViewState;
    }

    @Override
    public void setViewState(ButtonViewState state) {
        currentViewState = state;

        invalidateBackground();
    }

    @Override
    public VerticalAlign getVerticalAlign() {
        return verticalTextAlign;
    }

    @Override
    public void setVerticalAlign(VerticalAlign align) {
        this.verticalTextAlign = Checks.checkNotNull(align);
    }

}
