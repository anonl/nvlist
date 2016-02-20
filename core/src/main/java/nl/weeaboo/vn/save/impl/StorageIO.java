package nl.weeaboo.vn.save.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    public static IStorage read(IFileSystem fs, String filename) throws IOException {
        InputStream in = fs.openInputStream(filename);
        try {
            return read(in);
        } finally {
            in.close();
        }
    }

    public static IStorage read(SecureFileWriter fs, String filename) throws IOException {
        InputStream in = fs.newInputStream(filename);
        try {
            return read(in);
        } finally {
            in.close();
        }
    }

    public static IStorage read(InputStream in) throws IOException {
        Storage storage = new Storage();
        for (Entry<String, String> entry : PropertiesUtil.load(in).entrySet()) {
            storage.set(entry.getKey(), StoragePrimitive.fromJson(entry.getValue()));
        }
        return storage;
    }

    public static void write(IStorage storage, IWritableFileSystem fs, String filename) throws IOException {
        OutputStream out = fs.openOutputStream(filename, false);
        try {
            write(out, storage);
        } finally {
            out.close();
        }
    }

    public static void write(IStorage storage, SecureFileWriter fs, String filename) throws IOException {
        OutputStream out = fs.newOutputStream(filename, false);
        try {
            write(out, storage);
        } finally {
            out.close();
        }
    }

    public static void write(OutputStream out, IStorage storage) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : storage.getKeys()) {
            StoragePrimitive val = storage.get(key);
            if (val != null) {
                map.put(key, val.toJson());
            }
        }
        PropertiesUtil.save(out, map);
    }
}
