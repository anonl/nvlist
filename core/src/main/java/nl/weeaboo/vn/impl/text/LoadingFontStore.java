package nl.weeaboo.vn.impl.text;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.text.ILoadingFontStore;

final class LoadingFontStore implements ILoadingFontStore {

    private static final long serialVersionUID = TextImpl.serialVersionUID;

    private final FontResourceLoader resourceLoader;

    public LoadingFontStore(FontResourceLoader resourceLoader) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        String fontName = style.getFontName(FontResourceLoader.DEFAULT_FONT_NAME);
        ResourceId resourceId = resourceLoader.resolveResource(FilePath.of(fontName));

        FilePath absolutePath = resourceLoader.getAbsolutePath(resourceId.getFilePath());

        return StaticEnvironment.FONT_STORE.get().getFontMetrics(absolutePath, style);
    }

}
