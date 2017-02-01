package nl.weeaboo.vn.gdx.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public final class Scene2dUtil {

    private Scene2dUtil() {
    }

    /**
     * @see TextField#ENTER_ANDROID
     * @see TextField#ENTER_DESKTOP
     */
    public static boolean isEnterChar(char c) {
        return c == '\r' || c == '\n';
    }

}
