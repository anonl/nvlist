package nl.weeaboo.vn;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.google.common.collect.Iterables;

import nl.weeaboo.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITextureRenderer;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.SaveParams;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IRenderable;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.scene.ITransformable;
import nl.weeaboo.vn.scene.impl.EntityHelper;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.lua.LuaConsole;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    private LuaConsole luaConsole;

    public DebugControls(Scene2dEnv sceneEnv) {
        luaConsole = new LuaConsole(sceneEnv);
    }

    public void update(INovel novel) {
        IEnvironment env = novel.getEnv();
        IRenderEnv renderEnv = env.getRenderEnv();

        IContext activeContext = Iterables.get(env.getContextManager().getActiveContexts(), 0);
        IScriptContext scriptContext = null;
        IScreen screen = null;
        if (activeContext != null) {
            scriptContext = activeContext.getScriptContext();
            screen = activeContext.getScreen();
        }

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
            createImage(screen.getRootLayer(), imageModule);
        }
        if (screen != null && alt && Gdx.input.isKeyJustPressed(Keys.J)) {
            for (int n = 0; n < 100; n++) {
                createImage(screen.getRootLayer(), imageModule);
            }
        }

        // Text
        if (screen != null && alt && Gdx.input.isKeyJustPressed(Keys.T)) {
            createText(screen.getRootLayer());
        }

        // Button
        if (screen != null && alt && Gdx.input.isKeyJustPressed(Keys.B)) {
            createButton(screen.getRootLayer(), scriptContext);
        }

        // Music
        ISoundModule soundModule = env.getSoundModule();
        if (alt && Gdx.input.isKeyJustPressed(Keys.PERIOD)) {
            soundModule.getSoundController().stopAll();
        }
        if (alt && Gdx.input.isKeyJustPressed(Keys.M)) {
            try {
                ISound sound = soundModule.createSound(SoundType.MUSIC,
                        new ResourceLoadInfo("music.ogg"));
                sound.start(-1);
            } catch (IOException e) {
                LOG.warn("Audio error", e);
            }
        }

        // Lua console
        luaConsole.setActiveContext(activeContext);
        if (Gdx.input.isKeyJustPressed(Keys.F1)) {
            luaConsole.setVisible(!luaConsole.isVisible());
        }
    }

    public void update(ITransformable transformable) {
        IRenderable renderer = null;
        if (transformable instanceof IImageDrawable) {
            IImageDrawable image = (IImageDrawable)transformable;
            renderer = image.getRenderer();
        }

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.rotate(4);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.rotate(-4);
        } else if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyPressed(Keys.UP)) transformable.scale(1, 8 / 9.);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) transformable.scale(1, 1.125);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.scale(8 / 9., 1);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.scale(1.125, 1);
        } else if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            if (renderer instanceof ITextureRenderer) {
                ITextureRenderer texRenderer = (ITextureRenderer)renderer;
                if (Gdx.input.isKeyPressed(Keys.UP)) texRenderer.scrollUV(0, .05);
                if (Gdx.input.isKeyPressed(Keys.DOWN)) texRenderer.scrollUV(0, -.05);
                if (Gdx.input.isKeyPressed(Keys.LEFT)) texRenderer.scrollUV(.05, 0);
                if (Gdx.input.isKeyPressed(Keys.RIGHT)) texRenderer.scrollUV(-.05, 0);
            }
        } else {
            if (Gdx.input.isKeyPressed(Keys.UP)) transformable.translate(0, 5);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) transformable.translate(0, -5);
            if (Gdx.input.isKeyPressed(Keys.LEFT)) transformable.translate(-5, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) transformable.translate(5, 0);
        }
    }

    private static void createImage(ILayer layer, IImageModule imageModule) {
        IImageDrawable image = imageModule.createImage(layer);
        image.setPos(640, 360);
        image.setAlign(.5, .5);
        image.setTexture(imageModule.getTexture(new ResourceLoadInfo("test.jpg"), false));
    }

    private static void createButton(ILayer layer, IScriptContext scriptContext) {
        EntityHelper entityHelper = new EntityHelper();
        IButton button = entityHelper.createButton(layer, scriptContext);
        button.setText("Test");
    }

    private static void createText(ILayer layer) {
        EntityHelper entityHelper = new EntityHelper();
        ITextDrawable text = entityHelper.createText(layer);

        text.setBounds(200, 200, 800, 200);
        text.setZ((short)-1000);
        text.setDefaultStyle(new TextStyle(null, 32));
        text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        text.setVisibleText(0f);
    }

}
