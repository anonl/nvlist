package nl.weeaboo.vn.impl.save;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.settings.PropertiesUtil;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.StoragePrimitive;

/** Helper class for reading/writing {@link IStorage} objects to files */
public final class StorageIO {

    private StorageIO() {
    }

    /**
     * Reads a storage object from a filesystem.
     *
     * @throws IOException If an I/O error occurs while trying to read the input.
     */
    public static IStorage read(IFileSystem fs, FilePath path) throws IOException {
        InputStream in = fs.openInputStream(path);
        try {
            return read(in);
        } finally {
            in.close();
        }
    }

    /**
     * Reads a storage object.
     *
     * @throws IOException If an I/O error occurs while trying to read the input.
     */
    public static IStorage read(SecureFileWriter fs, FilePath path) throws IOException {
        InputStream in = fs.newInputStream(path);
        try {
            return read(in);
        } finally {
            in.close();
        }
    }

    /**
     * Reads a storage object.
     *
     * @throws IOException If an I/O error occurs while trying to read the input.
     */
    public static IStorage read(InputStream in) throws IOException {
        Storage storage = new Storage();
        for (Entry<String, String> entry : PropertiesUtil.load(in).entrySet()) {
            storage.set(entry.getKey(), StoragePrimitive.fromJson(entry.getValue()));
        }
        return storage;
    }

    /**
     * Writes a storage object.
     *
     * @throws IOException If an I/O error occurs while trying to write to the output.
     */
    public static void write(IStorage storage, IWritableFileSystem fs, FilePath path) throws IOException {
        OutputStream out = fs.openOutputStream(path, false);
        try {
            write(out, storage);
        } finally {
            out.close();
        }
    }

    /**
     * Writes a storage object.
     *
     * @throws IOException If an I/O error occurs while trying to write to the output.
     */
    public static void write(IStorage storage, SecureFileWriter fs, FilePath path) throws IOException {
        OutputStream out = fs.newOutputStream(path, false);
        try {
            write(out, storage);
        } finally {
            out.close();
        }
    }

    /**
     * Writes a storage object.
     *
     * @throws IOException If an I/O error occurs while trying to write to the output.
     */
    public static void write(OutputStream out, IStorage storage) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String key : storage.getKeys()) {
            StoragePrimitive val = storage.get(key);
            if (val != null) {
                map.put(key, val.toJson());
            }
        }
        PropertiesUtil.save(out, map);
    }
}
