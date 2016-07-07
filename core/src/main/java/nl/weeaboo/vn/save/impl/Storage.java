package nl.weeaboo.vn.save.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.StoragePrimitive;

@CustomSerializable
public class Storage implements IStorage, Json.Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(Storage.class);

    private static final int SAVE_FORMAT_VERSION = 1;

    private transient Map<String, StoragePrimitive> properties;

    // Must have a public no-arg constructor for serialization
    public Storage() {
        properties = newPropertiesMap();
    }

    public Storage(IStorage storage) {
        this();

        for (String key : storage.getKeys()) {
            properties.put(key, storage.get(key));
        }
    }

    private static Map<String, StoragePrimitive> newPropertiesMap() {
        return new LinkedHashMap<String, StoragePrimitive>();
    }

    @Override
    public final void write(Json json) {
        for (Entry<String, StoragePrimitive> entry : properties.entrySet()) {
            try {
                json.getWriter().json(entry.getKey(), entry.getValue().toJson());
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }
    }

    @Override
    public final void read(Json json, JsonValue jsonData) {
        Map<String, StoragePrimitive> map = newPropertiesMap();
        for (JsonValue child : jsonData) {
            if (child.name != null) {
                StoragePrimitive val = StoragePrimitive.fromJson(child.asString());
                map.put(child.name, val);
            }
        }

        properties.clear();
        properties.putAll(map);
    }

    public static String toJson(IStorage storage) {
        Storage serializable = new Storage(storage);
        return JsonUtil.toJson(serializable);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(SAVE_FORMAT_VERSION);
        out.writeUTF(Storage.toJson(this));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        properties = newPropertiesMap();

        int version = in.readInt();
        if (version != SAVE_FORMAT_VERSION) {
            throw new IOException("Unsupported serialized form: version=" + version);
        }

        Json json = JsonUtil.newJson();
        JsonValue jsonData = JsonUtil.parse(in.readUTF());
        read(json, jsonData);
    }

    private static void checkKey(String key) {
        Checks.checkNotNull(key, "Key must not be null");
    }

    protected void onChanged() {
    }

    @Override
    public void clear() {
        LOG.trace("{}: Clearing storage", this);

        properties.clear();
        onChanged();
    }

    @Override
    public StoragePrimitive remove(String key) {
        StoragePrimitive removed = properties.remove(key);
        if (removed != null) {
            LOG.trace("{}: Remove {}", this, key);
            onChanged();
        }
        return removed;
    }

    @Override
    public void addAll(IStorage val) {
        addAll("", val);
    }

    @Override
    public void addAll(String key, IStorage val) {
        //Be careful that adding an object to itself doesn't cause problems
        String prefix = (key.length() > 0 ? key + "." : key);
        IStorage s = val;
        for (String subkey : s.getKeys()) {
            set(prefix + subkey, s.get(subkey));
        }
    }

    @Override
    public Collection<String> getKeys() {
        return getKeys(null);
    }

    @Override
    public Collection<String> getKeys(String prefix) {
        List<String> result = new ArrayList<String>();
        for (String key : properties.keySet()) {
            if (prefix == null || key.startsWith(prefix)) {
                result.add(key);
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public boolean contains(String key) {
        return get(key) != null;
    }

    @Override
    public StoragePrimitive get(String key) {
        checkKey(key);

        StoragePrimitive result = properties.get(key);

        LOG.trace("{}: get(key={}) = {}", this, key, result);

        return result;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        StoragePrimitive obj = get(key);

        if (obj != null) {
            return obj.toBoolean(defaultValue);
        }
        return defaultValue;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return (int)getDouble(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return (long)getDouble(key, defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        StoragePrimitive obj = get(key);

        if (obj != null) {
            return obj.toDouble(defaultValue);
        }
        return defaultValue;
    }

    @Override
    public String getString(String key, String defaultValue) {
        StoragePrimitive obj = get(key);

        if (obj != null) {
            return obj.toString(defaultValue);
        }
        return defaultValue;
    }

    @Override
    public void set(String key, StoragePrimitive val) {
        checkKey(key);

        StoragePrimitive oldval;
        if (val == null) {
            oldval = remove(key);
        } else {
            oldval = properties.put(key, val);
        }

        LOG.trace("{}: set(key={}) = {}", this, key, val);

        if (oldval != val && (oldval == null || !oldval.equals(val))) {
            onChanged();
        }
    }

    @Override
    public void setBoolean(String key, boolean val) {
        set(key, StoragePrimitive.fromBoolean(val));
    }

    @Override
    public void setInt(String key, int val) {
        setDouble(key, val);
    }

    @Override
    public void setLong(String key, long val) {
        setDouble(key, val);
    }

    @Override
    public void setDouble(String key, double val) {
        set(key, StoragePrimitive.fromDouble(val));
    }

    @Override
    public void setString(String key, String val) {
        set(key, val != null ? StoragePrimitive.fromString(val) : null);
    }

}
