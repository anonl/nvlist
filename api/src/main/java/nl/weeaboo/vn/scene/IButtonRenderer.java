package nl.weeaboo.vn.scene;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;

public interface IButtonRenderer extends IRenderable {

    StyledText getText();
    void setText(StyledText stext);

    void setTexture(ButtonViewState viewState, ITexture tex);
    void setTexture(ButtonViewState viewState, INinePatch tex);

    void setViewState(ButtonViewState viewState);

    VerticalAlign getVerticalAlign();
    void setVerticalAlign(VerticalAlign align);
    
}
