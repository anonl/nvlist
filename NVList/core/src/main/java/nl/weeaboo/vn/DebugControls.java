package nl.weeaboo.vn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.Iterables;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.ITransformablePart;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.BasicPartRegistry;
import nl.weeaboo.vn.core.impl.TransformablePart;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IImagePart;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.SaveParams;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    public void update(INovel novel) {
        IEnvironment env = novel.getEnv();
        IRenderEnv renderEnv = env.getRenderEnv();
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

        // Fullscreen toggle
        if (alt && Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            DisplayMode dm = Gdx.graphics.getDesktopDisplayMode();
            if (!Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setDisplayMode(dm.width, dm.height, true);
            } else {
                Gdx.graphics.setDisplayMode(renderEnv.getWidth(), renderEnv.getHeight(), false);
            }
        }

        // Image
        IImageModule imageModule = env.getImageModule();
        if (screen != null && alt && Gdx.input.isKeyJustPressed(Keys.I)) {
            createImage(screen.getRootLayer(), imageModule, pr);
        }
        if (screen != null && alt && Gdx.input.isKeyJustPressed(Keys.J)) {
            for (int n = 0; n < 100; n++) {
                createImage(screen.getRootLayer(), imageModule, pr);
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
                e.getPart(pr.sound).start(-1);
            } catch (IOException e) {
                LOG.warn("Audio error", e);
            }
        }
    }

    public void update(ITransformablePart transformable, IImagePart image) {
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.rotate(4);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.rotate(-4);
        } else if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.UP)) transformable.scale(1, 8 / 9.);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) transformable.scale(1, 1.125);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.scale(8 / 9., 1);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.scale(1.125, 1);
        } else if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.UP)) image.scrollUV(0, .05);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) image.scrollUV(0, -.05);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) image.scrollUV(.05, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) image.scrollUV(-.05, 0);
        } else {
            if (Gdx.input.isKeyPressed(Keys.UP)) transformable.translate(0, 5);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) transformable.translate(0, -5);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.translate(-5, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.translate(5, 0);
        }
    }

    private static void createImage(ILayer layer, IImageModule imageModule, BasicPartRegistry pr) {
        Entity entity = imageModule.createImage(layer);
        TransformablePart transformable = entity.getPart(pr.transformable);
        transformable.setPos(640, 360);
        transformable.setZ((short)entity.getId());
        ImagePart image = entity.getPart(pr.image);
        image.setTexture(imageModule.getTexture(new ResourceLoadInfo("test.jpg"), false), 5);
    }

}
