package nl.weeaboo.gdx.res;

import com.badlogic.gdx.utils.Disposable;

public final class DisposeUtil {

    private DisposeUtil() {
    }

    /**
     * Null-safe dispose method
     *
     * @return {@code null}, so you can write: {@code object = DisposeUtil.dispose(object);}
     */
    public static <T extends Disposable> T dispose(T disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
        return null;
    }

}
