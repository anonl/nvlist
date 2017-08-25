package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.Disposable;

public final class DisposeUtil {

    private DisposeUtil() {
    }

    /**
     * Null-safe dispose method
     *
     * @return {@code null}, so you can write: {@code object = DisposeUtil.dispose(object);}
     */
    public static @Nullable <T extends Disposable> T dispose(@Nullable T disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
        return null;
    }

}
