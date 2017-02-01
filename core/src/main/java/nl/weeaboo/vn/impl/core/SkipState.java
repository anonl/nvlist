package nl.weeaboo.vn.impl.core;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;

public final class SkipState implements ISkipState {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private SkipMode skipMode = SkipMode.NONE;

    private boolean skipUnread = false;

    @Override
    public boolean isSkipping() {
        return getSkipMode() != SkipMode.NONE;
    }

    @Override
    public SkipMode getSkipMode() {
        return skipMode;
    }

    @Override
    public void skip(SkipMode mode) {
        setSkipMode(SkipMode.max(getSkipMode(), mode));
    }

    @Override
    public void setSkipMode(SkipMode mode) {
        skipMode = Checks.checkNotNull(mode);

        if (mode == SkipMode.NONE) {
            skipUnread = false;
        }
    }

    @Override
    public final void stopSkipping() {
        setSkipMode(SkipMode.NONE);
    }

    @Override
    public void handleInput(IInput input) {
        SkipMode skipMode = getSkipMode();
        if (skipMode == SkipMode.PARAGRAPH && !isSkipKeyHeld(input)) {
            // Stop skipping
            setSkipMode(SkipMode.NONE);
        } else if (skipMode == SkipMode.NONE && isSkipKeyHeld(input)) {
            // Start skipping
            setSkipMode(SkipMode.PARAGRAPH);

            // TODO: Implement a proper alt skip vkey that triggers skip-all instead of abusing textContinue
            if (input.isPressed(VKey.TEXT_CONTINUE, true)) {
                skipUnread = true;
            }
        }
    }

    private boolean isSkipKeyHeld(IInput input) {
        return input.isPressed(VKey.SKIP, false);
    }

    @Override
    public boolean shouldSkipLine(boolean lineRead) {
        if (!isSkipping()) {
            return false;
        }
        return lineRead || skipUnread;
    }

}
