package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontStore;

public interface ILoadingFontStore extends Serializable, IFontStore {

    /**
     * The default text style for on-screen text.
     *
     * @see ITextRenderer#getDefaultStyle()
     */
    TextStyle getDefaultStyle();

    /**
     * @see #getDefaultStyle()
     */
    void setDefaultStyle(TextStyle style);

}
