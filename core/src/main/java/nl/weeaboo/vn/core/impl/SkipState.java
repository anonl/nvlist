package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;

final class SkipState implements ISkipState {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private SkipMode skipMode = SkipMode.NONE;

    // TODO: Implement an alt skip key that toggles between skip-read and skip-all
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
    public void setSkipMode(SkipMode mode) {
        skipMode = Checks.checkNotNull(mode);
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
