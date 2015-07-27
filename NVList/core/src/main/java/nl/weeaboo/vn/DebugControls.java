package nl.weeaboo.vn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.Iterables;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.core.impl.TransformablePart;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.SaveParams;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    public void update(INovel novel) {
        IEnvironment env = novel.getEnv();
        BasicPartRegistry pr = (BasicPartRegistry)env.getPartRegistry();
        IContext activeContext = Iterables.get(env.getContextManager().getActiveContexts(), 0);
        IScreen screen = (activeContext != null ? activeContext.getScreen() : null);
        boolean alt = Gdx.input.isKeyPressed(Keys.ALT_LEFT);

        // Reset
        if (Gdx.input.isKeyJustPressed(Keys.F5)) {
            try {
                novel.restart();
            } catch (InitException e) {
                LOG.error("Fatal error during restart", e);
            }
        }

        // Save/load
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

        // Music
        ISoundModule soundModule = env.getSoundModule();
        if (alt && Gdx.input.isKeyJustPressed(Keys.PERIOD)) {
            soundModule.getSoundController().stopAll();
        }
        if (alt && Gdx.input.isKeyJustPressed(Keys.M)) {
            try {
                Entity e = soundModule.createSound(screen, SoundType.MUSIC,
                        new ResourceLoadInfo("music.ogg"));
                e.getPart(pr.sound).start();
            } catch (IOException e) {
                LOG.warn("Audio error", e);
            }
        }
    }

    public void update(TransformablePart part) {
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) part.rotate(4);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) part.rotate(-4);
        } else if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.UP)) part.scale(1, 8 / 9.);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) part.scale(1, 1.125);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) part.scale(8 / 9., 1);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) part.scale(1.125, 1);
        } else {
            if (Gdx.input.isKeyPressed(Keys.UP)) part.translate(0, 5);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) part.translate(0, -5);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) part.translate(-5, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) part.translate(5, 0);
        }
    }

}
