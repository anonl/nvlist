package nl.weeaboo.vn.buildtools.optimizer.image;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.base.Preconditions;

import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

final class ImageWithDef implements Disposable {

    private final Pixmap pixmap;
    private final ImageDefinition def;

    private volatile boolean disposed;

    public ImageWithDef(Pixmap pixmap, ImageDefinition def) {
        this.pixmap = pixmap;
        this.def = def;
    }

    @Override
    public void dispose() {
        disposed = true;

        pixmap.dispose();
    }

    public Pixmap getPixmap() {
        Preconditions.checkState(!disposed);

        return pixmap;
    }

    public ImageDefinition getDef() {
        Preconditions.checkState(!disposed);

        return def;
    }

}
