package nl.weeaboo.vn.impl.scene;

import java.util.EnumMap;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButtonRenderer;
import nl.weeaboo.vn.scene.IDrawable;

public class ButtonRendererMock extends AbstractRenderable implements IButtonRenderer {

    private static final long serialVersionUID = 1L;

    private StyledText text = StyledText.EMPTY_STRING;
    private Dim size = Dim.of(100, 20);
    private ButtonViewState viewState = ButtonViewState.DEFAULT;
    private VerticalAlign valign = VerticalAlign.MIDDLE;

    private final EnumMap<ButtonViewState, ITexture> regularTextures = new EnumMap<>(ButtonViewState.class);
    private final EnumMap<ButtonViewState, INinePatch> ninePatchTextures = new EnumMap<>(ButtonViewState.class);

    @Override
    public double getNativeWidth() {
        return size.w;
    }

    @Override
    public double getNativeHeight() {
        return size.h;
    }

    @Override
    public StyledText getText() {
        return text;
    }

    @Override
    public void setText(StyledText stext) {
        this.text = Checks.checkNotNull(stext);
    }

    @Override
    public ButtonViewState getViewState() {
        return viewState;
    }

    @Override
    public void setViewState(ButtonViewState viewState) {
        this.viewState = Checks.checkNotNull(viewState);
    }

    @Override
    public VerticalAlign getVerticalAlign() {
        return valign;
    }

    @Override
    public void setVerticalAlign(VerticalAlign align) {
        this.valign = Checks.checkNotNull(align);
    }

    /**
     * Returns the texture associated with the given view state. If that state uses a nine-patch, or no texture was set,
     * {@code null} is returned instead.
     *
     * @see #setTexture(ButtonViewState, ITexture)
     */
    public ITexture getRegularTexture(ButtonViewState viewState) {
        return regularTextures.get(viewState);
    }

    /**
     * Returns the nine-patch associated with the given view state. If that state uses a texture, or no nine-patch was
     * set, {@code null} is returned instead.
     *
     * @see #setTexture(ButtonViewState, INinePatch)
     */
    public INinePatch getNinePatchTexture(ButtonViewState viewState) {
        return ninePatchTextures.get(viewState);
    }

    @Override
    public void setTexture(ButtonViewState viewState, ITexture tex) {
        regularTextures.put(viewState, tex);
        ninePatchTextures.remove(viewState);
    }

    @Override
    public void setTexture(ButtonViewState viewState, INinePatch tex) {
        regularTextures.remove(viewState);
        ninePatchTextures.put(viewState, tex);
    }

    @Override
    protected void render(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        // Not implemented
    }

}
