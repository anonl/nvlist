package nl.weeaboo.vn.impl.text;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IResourceResolver;
import nl.weeaboo.vn.impl.core.AbstractModule;
import nl.weeaboo.vn.text.ILoadingFontStore;
import nl.weeaboo.vn.text.ITextLog;
import nl.weeaboo.vn.text.ITextModule;

public class TextModule extends AbstractModule implements ITextModule {

    private static final long serialVersionUID = TextImpl.serialVersionUID;

    private final ITextLog textLog;
    private final FontResourceLoader fontLoader;
    private final ILoadingFontStore fontStore;

    public TextModule(IEnvironment env) {
        this(new TextLog(), new FontResourceLoader(env));
    }

    public TextModule(ITextLog textLog, FontResourceLoader fontLoader) {
        this.textLog = Checks.checkNotNull(textLog);
        this.fontLoader = Checks.checkNotNull(fontLoader);
        this.fontStore = new LoadingFontStore(fontLoader);
    }

    @Override
    public ITextLog getTextLog() {
        return textLog;
    }

    @Override
    public IResourceResolver getFontLoader() {
        return fontLoader;
    }

    @Override
    public ILoadingFontStore getFontStore() {
        return fontStore;
    }

}
