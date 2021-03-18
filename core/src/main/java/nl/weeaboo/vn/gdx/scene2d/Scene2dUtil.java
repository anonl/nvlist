package nl.weeaboo.vn.gdx.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldClickListener;

/**
 * Various functions related to GDX scene2d.
 */
public final class Scene2dUtil {

    private Scene2dUtil() {
    }

    /**
     * @see TextFieldClickListener#keyTyped
     */
    public static boolean isEnterChar(char c) {
        return c == '\r' || c == '\n';
    }

}
