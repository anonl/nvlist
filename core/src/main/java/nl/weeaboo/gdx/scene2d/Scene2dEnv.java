package nl.weeaboo.gdx.scene2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.gdx.res.GdxFileSystem;

public class Scene2dEnv implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(Scene2dEnv.class);

    private final Stage stage;
    private final Skin skin;

    public Scene2dEnv(GdxFileSystem fileSystem, Viewport viewport) {
        stage = new Stage(viewport);

        skin = loadSkin(fileSystem);
    }

    private Skin loadSkin(GdxFileSystem fileSystem) {
        FileHandle skinFile = fileSystem.resolve("skin/uiskin.json");
        if (!skinFile.exists()) {
            LOG.warn("Skin file not found: {}", skinFile);
        } else {
            try {
                return new Skin(skinFile);
            } catch (SerializationException se) {
                LOG.error("Error loading Scene2d skin", se);
            }
        }
        return new Skin();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);

        stage.dispose();
        skin.dispose();
    }

    public void draw() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

}
