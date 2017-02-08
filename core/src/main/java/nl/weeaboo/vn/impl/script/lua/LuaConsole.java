package nl.weeaboo.vn.impl.script.lua;

import java.util.Deque;

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
import nl.weeaboo.vn.gdx.scene2d.Scene2dEnv;
import nl.weeaboo.vn.gdx.scene2d.Scene2dUtil;
import nl.weeaboo.vn.script.ScriptException;

public class LuaConsole {

    private static final Logger LOG = LoggerFactory.getLogger(LuaConsole.class);
    private static final int INPUT_BUFFER_LIMIT = 16;

    private final Scene2dEnv sceneEnv;

    private final StringBuilder log = new StringBuilder("*** Lua console ***");
    private Deque<String> inputBuffer = Queues.newArrayDeque();
    private int inputBufferIndex;
    private IContext activeContext;

    private Table layout;
    private TextArea console;
    private TextField inputField;
    private boolean visible;

    public LuaConsole(Scene2dEnv sceneEnv) {
        this.sceneEnv = sceneEnv;
    }

    /**
     * @return {@code true} if the console is currently visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /** Change visibility of the console GUI */
    public void setVisible(boolean v) {
        if (visible != v) {
            visible = v;

            if (v) {
                show();
            } else {
                hide();
            }
        }
    }

    private void show() {
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
                    setVisible(false);
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

    private void hide() {
        if (layout != null) {
            layout.remove();
            layout = null;
        }
        activeContext = null;
    }

    protected void eval(String luaCode) {
        if (activeContext == null) {
            append("No script context active");
        } else {
            try {
                String result = LuaScriptUtil.eval(activeContext, luaCode);
                append("> " + result);
            } catch (ScriptException e) {
                LOG.info("Error during eval", e);
                append("> Error: " + e.getLocalizedMessage());
            }
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

    /** Sets the active context. The Lua console executes commands in this context. */
    public void setActiveContext(IContext context) {
        activeContext = context;
    }

}
