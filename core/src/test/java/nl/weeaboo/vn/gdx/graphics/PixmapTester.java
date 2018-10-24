package nl.weeaboo.vn.gdx.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Disposable;

public final class PixmapTester implements Disposable {

    private final List<Pixmap> allocatedPixmaps = new ArrayList<>();

    @Override
    public void dispose() {
        allocatedPixmaps.forEach(Pixmap::dispose);
        allocatedPixmaps.clear();
    }

    public Pixmap newPixmap(Format format, Color color) {
        Pixmap pixmap = PixmapUtil.newUninitializedPixmap(3, 3, format);
        pixmap.setColor(color);
        pixmap.fill();
        allocatedPixmaps.add(pixmap);
        return pixmap;
    }

}
