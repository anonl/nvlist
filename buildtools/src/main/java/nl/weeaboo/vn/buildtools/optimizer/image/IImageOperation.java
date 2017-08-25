package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

interface IImageOperation {

    ImageWithDef optimize(ImageWithDef original) throws IOException;

}
