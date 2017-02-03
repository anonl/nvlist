package nl.weeaboo.vn.impl.text;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.text.ITextLog;
import nl.weeaboo.vn.text.ITextModule;

public class TextModule implements ITextModule {

    private static final long serialVersionUID = TextImpl.serialVersionUID;

    private final ITextLog textLog;

    public TextModule() {
        this(new TextLog());
    }

    public TextModule(TextLog textLog) {
        this.textLog = Checks.checkNotNull(textLog);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public ITextLog getTextLog() {
        return textLog;
    }

}
