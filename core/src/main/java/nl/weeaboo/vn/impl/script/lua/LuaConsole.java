package nl.weeaboo.vn.impl.script.lua;

import java.util.Deque;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.collect.Iterables;
import com.google.common.collect.Queues;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.vn.gdx.scene2d.Scene2dUtil;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Default implementation of {@link ILuaConsole}.
 */
public class LuaConsole implements ILuaConsole {

    private static final Logger LOG = LoggerFactory.getLogger(LuaConsole.class);
    private static final int INPUT_BUFFER_LIMIT = 16;

    private final Scene2dEnv sceneEnv;

    private final StringBuilder log = new StringBuilder("*** Lua console ***");
    private Deque<String> inputBuffer = Queues.newArrayDeque();
    private int inputBufferIndex;
    private @Nullable IContextManager contextManager;

    private @Nullable Table layout;
    private TextArea console;
    private TextField inputField;
    private boolean visible;

    public LuaConsole(Scene2dEnv sceneEnv) {
        this.sceneEnv = sceneEnv;
    }

    @Override
    public void update(IEnvironment env, INativeInput input) {
        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        if (input.consumePress(KeyCode.F1)) {
            if (isVisible()) {
                close();
            } else {
                open(env.getContextManager());
            }
        }
    }

    public void open(IContextManager contextManager) {
        this.contextManager = contextManager;
        visible = true;

        final Stage stage = sceneEnv.getStage();
        final Skin skin = sceneEnv.getSkin();

        console = new TextArea(log.toString(), skin);
        console.setDisabled(true);
        console.setFocusTraversal(false);

        inputField = new TextField(Iterables.getLast(inputBuffer, ""), skin);
        inputField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.UP) {
                    return updateInputBufferIndex(-1);
                } else if (keycode == Keys.DOWN) {
                    return updateInputBufferIndex(1);
                }
                return false;
            }
        });
        inputField.setTextFieldListener(new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (Scene2dUtil.isEnterChar(c)) {
                    evalInput();
                }
            }
        });

        ImageTextButton inputButton = new ImageTextButton("Eval", skin);
        inputButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                evalInput();
            }
        });

        float pad = Math.min(stage.getWidth(), stage.getHeight()) / 10;

        layout = new Table(skin);
        float layoutW = stage.getWidth() / 2 - pad * 2;
        float layoutH = stage.getHeight() - pad * 2;
        layout.setBounds(stage.getWidth() - layoutW - pad, pad, layoutW, layoutH);
        layout.add(console).colspan(2).expand().fill();
        layout.row();
        layout.add(inputField).bottom().expandX().fill();
        layout.add(inputButton).bottom().fill();
        layout.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.F1) {
                    close();
                    return true;
                }
                return false;
            }

        });

        stage.addActor(layout);
        stage.setKeyboardFocus(inputField);
    }

    private void evalInput() {
        String text = inputField.getText();
        if (text.trim().isEmpty()) {
            return;
        }

        append(text);

        inputBuffer.addLast(text);
        while (inputBuffer.size() > INPUT_BUFFER_LIMIT) {
            inputBuffer.removeFirst();
        }
        inputBufferIndex = inputBuffer.size();
        inputField.setText("");

        eval(text);
    }

    public void close() {
        visible = false;
        if (layout != null) {
            layout.remove();
            layout = null;
        }
        contextManager = null;
    }

    protected void eval(String luaCode) {
        if (contextManager == null) {
            append("No script context active");
            return;
        }

        IContext context = contextManager.getPrimaryContext();
        if (context == null) {
            append("No script context active");
            return;
        }

        try {
            String result = LuaScriptUtil.eval(contextManager,
                    (LuaScriptThread)context.getScriptContext().getMainThread(), luaCode);
            append("> " + result);
        } catch (ScriptException e) {
            LOG.info("Error during eval", e);
            append("> Error: " + e.getLocalizedMessage());
        }
    }

    private boolean updateInputBufferIndex(int dir) {
        inputBufferIndex += dir;
        if (inputBufferIndex < 0) {
            inputBufferIndex = 0;
            return false;
        } else if (inputBufferIndex >= inputBuffer.size()) {
            inputBufferIndex = inputBuffer.size();
            inputField.setText("");
            return false;
        }

        inputField.setText(Iterables.get(inputBuffer, inputBufferIndex));
        return true;
    }

    protected void append(String str) {
        log.append("\n").append(str);
        console.appendText("\n" + str);
    }

    /**
     * @return {@code true} if the console is currently visible.
     */
    public boolean isVisible() {
        return visible;
    }

}
