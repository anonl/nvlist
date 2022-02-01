package nl.weeaboo.vn.scene;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;

/**
 * Renderer for {@link IButton}.
 */
public interface IButtonRenderer extends IRenderable {

    /**
     * @return The button label text, or {@link StyledText#EMPTY_STRING} if no label is defined.
     */
    StyledText getText();

    /**
     * Sets the button label text.
     */
    void setText(StyledText stext);

    /**
     * Returns the background image for the button (if it exists).
     */
    @CheckForNull
    INinePatch getTexture(ButtonViewState viewState);

    /**
     * Sets the button background to the supplied texture.
     */
    void setTexture(ButtonViewState viewState, ITexture tex);

    /**
     * Sets the button background to the supplied nine-patch.
     */
    void setTexture(ButtonViewState viewState, @Nullable INinePatch tex);

    /**
     * @return The current button state.
     */
    ButtonViewState getViewState();

    /**
     * Sets the button state.
     */
    void setViewState(ButtonViewState viewState);

    /**
     * Returns the vertical alignment of the button label.
     */
    VerticalAlign getVerticalAlign();

    /**
     * Sets the vertical alignment of the button label.
     */
    void setVerticalAlign(VerticalAlign align);

}
