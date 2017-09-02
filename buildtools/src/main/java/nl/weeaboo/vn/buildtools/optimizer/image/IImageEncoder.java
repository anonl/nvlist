package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.File;
import java.io.IOException;

public interface IImageEncoder {

    void encode(ImageWithDef image, File outputFile) throws IOException;

}
