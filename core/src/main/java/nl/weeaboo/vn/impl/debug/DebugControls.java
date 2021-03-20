package nl.weeaboo.vn.impl.debug;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.res.NativeMemoryTracker;
import nl.weeaboo.vn.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.vn.impl.save.SaveParams;
import nl.weeaboo.vn.impl.script.lua.LuaConsole;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.save.ISaveModule;

/**
 * Activates special functionality only available in debug mode (see {@link NovelPrefs#DEBUG}).
 */
public final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    private final LuaConsole luaConsole;
    private final ScreenshotTaker screenshotTaker;

    public DebugControls(Scene2dEnv sceneEnv) {
        this.luaConsole = new LuaConsole(sceneEnv);
        this.screenshotTaker = new ScreenshotTaker();
    }

    /**
     * Handle input and update internal state.
     */
    public void update(INovel novel, INativeInput input) {
        IEnvironment env = novel.getEnv();
        screenshotTaker.update(env, input);

        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        final boolean ctrl = input.isPressed(KeyCode.CONTROL_LEFT, true) ||
                input.isPressed(KeyCode.CONTROL_RIGHT, true);

        // Restart
        ISystemModule systemModule = env.getSystemModule();
        if (ctrl && input.consumePress(KeyCode.F5)) {
            try {
                systemModule.restart();
            } catch (InitException e) {
                LOG.error("Fatal error during restart", e);
            }
        }

        // Clear caches
        if (input.consumePress(KeyCode.F3)) {
            novel.getEnv().clearCaches();
            LOG.debug("{}", NativeMemoryTracker.get().getSummary());
        }

        // Save/load
        ISaveModule saveModule = env.getSaveModule();
        int slot = saveModule.getQuickSaveSlot(99);
        if (input.consumePress(KeyCode.NUMPAD_ADD)) {
            try {
                saveModule.save(novel, slot, new SaveParams());
            } catch (IOException e) {
                LOG.warn("Save error", e);
            }
        } else if (input.consumePress(KeyCode.NUMPAD_SUBTRACT)) {
            try {
                saveModule.load(novel, slot);
            } catch (IOException e) {
                LOG.warn("Load error", e);
            }
        }

        // Lua console
        luaConsole.setContext(env.getContextManager());
        if (input.consumePress(KeyCode.F1)) {
            // TODO: LuaConsole needs to intercept the F1 key in order to hide itself
            luaConsole.setVisible(!luaConsole.isVisible());
        }
    }

}
