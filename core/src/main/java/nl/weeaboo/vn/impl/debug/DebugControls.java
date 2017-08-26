package nl.weeaboo.vn.impl.debug;

import java.io.IOException;
import java.util.Random;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

import nl.weeaboo.common.Insets2D;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.gdx.graphics.GdxBitmapTweenRenderer;
import nl.weeaboo.vn.gdx.graphics.GdxCrossFadeRenderer;
import nl.weeaboo.vn.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.INinePatch.AreaId;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig;
import nl.weeaboo.vn.impl.image.BitmapTweenConfig.ControlImage;
import nl.weeaboo.vn.impl.image.CrossFadeConfig;
import nl.weeaboo.vn.impl.image.NinePatchRenderer;
import nl.weeaboo.vn.impl.save.SaveParams;
import nl.weeaboo.vn.impl.scene.ComponentFactory;
import nl.weeaboo.vn.impl.script.lua.LuaConsole;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.render.RenderUtil;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public final class DebugControls {

    private static final Logger LOG = LoggerFactory.getLogger(DebugControls.class);

    private final LuaConsole luaConsole;

    public DebugControls(Scene2dEnv sceneEnv) {
        this.luaConsole = new LuaConsole(sceneEnv);
    }

    /**
     * Handle input and update internal state.
     */
    public void update(INovel novel, INativeInput input) {
        IEnvironment env = novel.getEnv();
        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        IContext activeContext = env.getContextManager().getPrimaryContext();
        IScriptContext scriptContext = null;
        IScreen screen = null;
        if (activeContext != null) {
            scriptContext = activeContext.getScriptContext();
            screen = activeContext.getScreen();
        }

        boolean alt = input.isPressed(KeyCode.ALT_LEFT, true);

        // Reset
        ISystemModule systemModule = env.getSystemModule();
        if (input.consumePress(KeyCode.F5)) {
            try {
                systemModule.restart();
            } catch (InitException e) {
                LOG.error("Fatal error during restart", e);
            }
        }

        // Save/load
        ISaveModule saveModule = env.getSaveModule();
        int slot = saveModule.getQuickSaveSlot(99);
        if (input.consumePress(KeyCode.PLUS)) {
            LOG.debug("Save");
            SaveParams saveParams = new SaveParams();
            try {
                saveModule.save(novel, slot, saveParams, null);
            } catch (SaveFormatException e) {
                LOG.warn("Save error", e);
            } catch (IOException e) {
                LOG.warn("Save error", e);
            }
        } else if (input.consumePress(KeyCode.MINUS)) {
            LOG.debug("Load");
            try {
                saveModule.load(novel, slot, null);
            } catch (SaveFormatException e) {
                LOG.warn("Load error", e);
            } catch (IOException e) {
                LOG.warn("Load error", e);
            }
        }

        // Image
        IImageModule imageModule = env.getImageModule();
        if (screen != null && alt && input.consumePress(KeyCode.I)) {
            createImage(screen.getRootLayer(), imageModule);
        }
        if (screen != null && alt && input.consumePress(KeyCode.J)) {
            for (int n = 0; n < 100; n++) {
                createImage(screen.getRootLayer(), imageModule);
            }
        }
        if (screen != null && alt && input.consumePress(KeyCode.K)) {
            createBitmapTweenImage(screen.getRootLayer(), imageModule);
        }
        if (screen != null && alt && input.consumePress(KeyCode.L)) {
            createCrossFadeImage(screen.getRootLayer(), imageModule);
        }
        if (screen != null && alt && input.consumePress(KeyCode.N)) {
            createNinePatchImage(screen.getRootLayer(), imageModule);
        }

        // Text
        if (screen != null && alt && input.consumePress(KeyCode.T)) {
            createText(screen.getRootLayer());
        }
        if (screen != null && alt && input.consumePress(KeyCode.Y)) {
            createLongText(screen.getRootLayer());
        }

        // Button
        if (screen != null && alt && input.consumePress(KeyCode.B)) {
            createButton(screen.getRootLayer(), imageModule, scriptContext);
        }

        // Music
        ISoundModule soundModule = env.getSoundModule();
        if (alt && input.consumePress(KeyCode.PERIOD)) {
            soundModule.getSoundController().stopAll();
        }
        if (alt && input.consumePress(KeyCode.M)) {
            try {
                ISound sound = soundModule.createSound(SoundType.MUSIC,
                        new ResourceLoadInfo(FilePath.of("music.ogg")));
                sound.start(2);
            } catch (IOException e) {
                LOG.warn("Audio error", e);
            }
        }

        // Lua console
        luaConsole.setActiveContext(activeContext);
        if (input.consumePress(KeyCode.F1)) {
            // TODO: LuaConsole needs to intercept the F1 key in order to hide itself
            luaConsole.setVisible(!luaConsole.isVisible());
        }
    }

    private static void createImage(ILayer layer, IImageModule imageModule) {
        IImageDrawable image = imageModule.createImage(layer);
        image.setPos(640, 360);
        image.setAlign(.5, .5);

        ITexture texture = getTestTexture(imageModule);
        image.setTexture(texture);
    }

    private static @Nullable ITexture getTestTexture(IImageModule imageModule) {
        return imageModule.getTexture(FilePath.of("test"));
    }

    private void createNinePatchImage(ILayer layer, IImageModule imageModule) {
        IImageDrawable image = imageModule.createImage(layer);
        image.setPos(640, 360);

        ITexture texture = getTestTexture(imageModule);
        NinePatchRenderer renderer = new NinePatchRenderer();
        renderer.setInsets(Insets2D.of(50));
        for (AreaId area : AreaId.values()) {
            renderer.setTexture(area, texture);
        }
        renderer.setSize(400, 400);
        image.setRenderer(renderer, Direction.CENTER);
    }

    private static void createButton(ILayer layer, IImageModule imageModule, IScriptContext scriptContext) {
        ComponentFactory entityHelper = new ComponentFactory();
        IButton button = entityHelper.createButton(layer, scriptContext);
        button.setSize(150, 150);
        button.setTexture(ButtonViewState.DEFAULT, getTestTexture(imageModule));
        button.setText("MgZx\nZxMg");
        button.setPos(800, 0);
    }

    private static void createText(ILayer layer) {
        ComponentFactory entityHelper = new ComponentFactory();
        ITextDrawable text = entityHelper.createText(layer);

        text.setBounds(200, 200, 800, 200);
        text.setZ((short)-1000);

        // CHECKSTYLE:OFF
        text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        // CHECKSTYLE:ON
    }

    private static void createLongText(ILayer layer) {
        ComponentFactory entityHelper = new ComponentFactory();
        ITextDrawable text = entityHelper.createText(layer);
        text.setZ((short)1000);
        text.setBounds(0, 0, 1280, 720);

        MutableTextStyle mts = new MutableTextStyle();
        mts.setColor(0xFF404040);
        mts.setSpeed(10f);
        text.setDefaultStyle(mts.immutableCopy());

        Random random = new Random();
        BaseEncoding encoder = BaseEncoding.base64().omitPadding();
        MutableStyledText mst = new MutableStyledText();
        for (int w = 0; w < 1000; w++) {
            byte[] bytes = new byte[3 + random.nextInt(5)];
            random.nextBytes(bytes);

            mts.setFontSize(16 + 16 * random.nextInt(2));

            switch (random.nextInt(10)) {
            case 0:
                mts.setFontStyle(EFontStyle.BOLD);
                break;
            case 1:
                mts.setFontStyle(EFontStyle.ITALIC);
                break;
            default:
                mts.setFontStyle(EFontStyle.PLAIN);
            }

            mts.setColor(RenderUtil.packRGBAtoARGB(random.nextFloat(), random.nextFloat(),
                    random.nextFloat(), 1f));

            mst.append(new StyledText(encoder.encode(bytes), mts.immutableCopy()));
            mst.append(' ', null);
        }
        text.setText(mst.immutableCopy());
    }

    private static void createBitmapTweenImage(ILayer layer, IImageModule imageModule) {
        IImageDrawable image = imageModule.createImage(layer);

        ITexture texture = getTestTexture(imageModule);
        ITexture control = imageModule.getTexture(FilePath.of("fade/shutter-right"));

        BitmapTweenConfig config = new BitmapTweenConfig(600, new ControlImage(control, false));
        config.setStartTexture(texture);
        config.setEndTexture(null);

        image.setRenderer(new GdxBitmapTweenRenderer(imageModule, config));
        image.setSize(256, 256);
    }

    private static void createCrossFadeImage(ILayer layer, IImageModule imageModule) {
        IImageDrawable image = imageModule.createImage(layer);

        CrossFadeConfig config = new CrossFadeConfig(600);
        config.setStartTexture(imageModule.getTexture(FilePath.of("alphablend")));
        config.setEndTexture(null);

        image.setRenderer(new GdxCrossFadeRenderer(imageModule, config));
        image.setSize(256, 256);
    }
}
