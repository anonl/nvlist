package nl.weeaboo.vn.buildtools.file;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;

/**
 * Represents an encoded resource file.
 */
public interface IEncodedResource extends Disposable {

    /**
     * Reads and returns the encoded bytes representing the resource.
     * @throws IOException If an I/O error occurs while trying to read the encoded data.
     */
    byte[] readBytes() throws IOException;

    /**
     * The encoded file size in bytes.
     */
    long getFileSize() throws IOException;

}
