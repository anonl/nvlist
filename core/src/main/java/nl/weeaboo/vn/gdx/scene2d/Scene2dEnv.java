package nl.weeaboo.vn.gdx.scene2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;

public class Scene2dEnv implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(Scene2dEnv.class);

    private final Stage stage;
    private final Skin skin;

    public Scene2dEnv(GdxFileSystem fileSystem, Viewport viewport) {
        stage = new Stage(viewport);

        skin = loadSkin(fileSystem);
    }

    private Skin loadSkin(GdxFileSystem fileSystem) {
        String skinPath = "skin/uiskin.json";

        FileHandle skinFile = null;
        if (fileSystem.getFileExists(FilePath.of(skinPath))) {
            skinFile = fileSystem.resolve(skinPath);
        } else {
            // Fallback: use the internal skin stored in the classpath
            skinFile = Gdx.files.classpath("builtin/" + skinPath);
        }

        // Load skin
        if (skinFile != null) {
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

    /** Draws the stage. */
    public void draw() {
        stage.draw();
    }

    /**
     * @return The scene2D stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @return The scene2D skin.
     */
    public Skin getSkin() {
        return skin;
    }

}
