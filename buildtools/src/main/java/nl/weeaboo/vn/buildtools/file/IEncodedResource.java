package nl.weeaboo.vn.buildtools.file;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;

public interface IEncodedResource extends Disposable {

    byte[] readBytes() throws IOException;

}
