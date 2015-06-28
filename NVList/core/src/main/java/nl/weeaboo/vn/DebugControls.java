package nl.weeaboo.vn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.SaveParams;

final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    public void update(INovel novel) {
        IEnvironment env = novel.getEnv();
        ISaveModule saveModule = env.getSaveModule();
        int slot = saveModule.getQuickSaveSlot(99);

        if (Gdx.input.isKeyJustPressed(Keys.PLUS)) {
            LOG.debug("Save");
            SaveParams saveParams = new SaveParams();
            try {
                saveModule.save(novel, slot, saveParams, null);
            } catch (SaveFormatException e) {
                LOG.warn("Save error", e);
            } catch (IOException e) {
                LOG.warn("Save error", e);
            }
        } else if (Gdx.input.isKeyJustPressed(Keys.MINUS)) {
            LOG.debug("Load");
            try {
                saveModule.load(novel, slot, null);
            } catch (SaveFormatException e) {
                LOG.warn("Load error", e);
            } catch (IOException e) {
                LOG.warn("Load error", e);
            }
        }
    }

}
