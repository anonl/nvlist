package nl.weeaboo.vn.impl.core;

import javax.annotation.Nullable;

import nl.weeaboo.vn.core.IDestructible;

public final class Destructibles {

    private Destructibles() {
    }

    /**
     * Null-safe destroy method
     *
     * @return {@code null}, so you can write: {@code object = Destructibles.destroy(object);}
     */
    public static @Nullable <T extends IDestructible> T destroy(@Nullable T destructibles) {
        if (destructibles != null) {
            destructibles.destroy();
        }
        return null;
    }

}
