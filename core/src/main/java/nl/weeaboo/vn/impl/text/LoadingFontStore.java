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

    private TextStyle defaultTextStyle = new TextStyle(null, 32);

    public LoadingFontStore(FontResourceLoader resourceLoader) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        final GdxFontStore fontStore = StaticEnvironment.FONT_STORE.get();

        String fontName = style.getFontName(TextUtil.DEFAULT_FONT_NAME);
        ResourceId resourceId = resourceLoader.resolveResource(FilePath.of(fontName));
        if (resourceId == null) {
            return fontStore.getFontMetrics(FilePath.empty(), style);
        }

        FilePath absolutePath = resourceLoader.getAbsolutePath(resourceId.getFilePath());
        return fontStore.getFontMetrics(absolutePath, style);
    }

    @Override
    public TextStyle getDefaultStyle() {
        return defaultTextStyle;
    }

    @Override
    public void setDefaultStyle(TextStyle style) {
        this.defaultTextStyle = Checks.checkNotNull(style);
    }

}
