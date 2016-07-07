package nl.weeaboo.vn.save.impl;

import java.util.Collection;

import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.StoragePrimitive;

/** Provides a read-only view of an {@link IStorage} object. */
public final class UnmodifiableStorage implements IStorage {

    private static final long serialVersionUID = 1L;

    private final IStorage inner;

    private UnmodifiableStorage(IStorage inner) {
        this.inner = inner;
    }

    public static UnmodifiableStorage from(IStorage storage) {
        return new UnmodifiableStorage(storage);
    }

    public static UnmodifiableStorage fromCopy(IStorage storage) {
        return new UnmodifiableStorage(new Storage(storage));
    }

    private static UnsupportedOperationException modificationException() {
        return new UnsupportedOperationException("Attempt to modify UnmodifiableStorage");
    }

    @Override
    public void clear() {
        throw modificationException();
    }

    @Override
    public StoragePrimitive remove(String key) {
        throw modificationException();
    }

    @Override
    public void addAll(IStorage val) {
        throw modificationException();
    }

    @Override
    public void addAll(String prefix, IStorage val) {
        throw modificationException();
    }

    @Override
    public Collection<String> getKeys() {
        return inner.getKeys();
    }

    @Override
    public Collection<String> getKeys(String prefix) {
        return inner.getKeys(prefix);
    }

    @Override
    public boolean contains(String key) {
        return inner.contains(key);
    }

    @Override
    public StoragePrimitive get(String key) {
        return inner.get(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return inner.getBoolean(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return inner.getInt(key, defaultValue);
    }

    @Override
    public long getLong(String keyTotal, long defaultValue) {
        return inner.getLong(keyTotal, defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return inner.getDouble(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return inner.getString(key, defaultValue);
    }

    @Override
    public void set(String key, StoragePrimitive val) {
        throw modificationException();
    }

    @Override
    public void setBoolean(String key, boolean val) {
        throw modificationException();
    }

    @Override
    public void setInt(String key, int val) {
        throw modificationException();
    }

    @Override
    public void setLong(String key, long val) {
        throw modificationException();
    }

    @Override
    public void setDouble(String key, double val) {
        throw modificationException();
    }

    @Override
    public void setString(String key, String val) {
        throw modificationException();
    }

}
