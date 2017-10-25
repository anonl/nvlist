package nl.weeaboo.vn.buildtools.optimizer.image;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Preconditions;

import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public final class ImageWithDef implements Disposable {

    private final Pixmap pixmap;
    private final ImageDefinition def;

    private volatile boolean disposed;

    public ImageWithDef(Pixmap pixmap, IImageDefinition def) {
        this.pixmap = pixmap;
        this.def = ImageDefinition.from(def);
    }

    @Override
    public void dispose() {
        disposed = true;

        pixmap.dispose();
    }

    /**
     * Returns the pixmap.
     *
     * @throws IllegalStateException If this object is disposed.
     */
    public Pixmap getPixmap() {
        Preconditions.checkState(!disposed);

        return pixmap;
    }

    /**
     * Returns the image definition.
     *
     * @throws IllegalStateException If this object is disposed.
     */
    public ImageDefinition getDef() {
        Preconditions.checkState(!disposed);

        return def;
    }

}
