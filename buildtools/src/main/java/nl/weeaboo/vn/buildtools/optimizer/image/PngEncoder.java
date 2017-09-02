package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

public final class PngEncoder implements IImageEncoder {

    @Override
    public void encode(ImageWithDef image, File outputFile) throws IOException {
        Pixmap pixmap = image.getPixmap();

        PixmapIO.writePNG(new FileHandle(outputFile), pixmap);
    }

}
