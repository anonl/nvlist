package nl.weeaboo.vn.impl.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;

public final class SkipState implements ISkipState {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private static final Logger LOG = LoggerFactory.getLogger(SkipState.class);

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

        if (input.isPressed(VKey.SCENE_SKIP, false)) {
            LOG.debug("Start skipping (scene)");
            skipUnread = true;
            setSkipMode(SkipMode.SCENE);
        }

        if (input.isPressed(VKey.SKIP, false) && !input.isPressed(VKey.ALT_SKIP, false)) {
            if (skipMode == SkipMode.NONE) {
                LOG.debug("Start skipping (regular)");

                setSkipMode(SkipMode.PARAGRAPH);
                skipUnread = false;
            }
        } else if (input.isPressed(VKey.ALT_SKIP, false)) {
            if (skipMode == SkipMode.NONE) {
                LOG.debug("Start skipping (alt)");

                setSkipMode(SkipMode.PARAGRAPH);
                skipUnread = true;
            }
        } else {
            if (skipMode == SkipMode.PARAGRAPH) {
                LOG.trace("Stop skipping");

                setSkipMode(SkipMode.NONE);
            }
        }
    }

    @Override
    public boolean shouldSkipLine(boolean lineRead) {
        if (!isSkipping()) {
            return false;
        }
        return lineRead || skipUnread;
    }

}
