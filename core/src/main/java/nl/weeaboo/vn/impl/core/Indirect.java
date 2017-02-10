package nl.weeaboo.vn.impl.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import nl.weeaboo.lua2.io.DelayedReader;
import nl.weeaboo.lua2.io.LuaSerializer;

/**
 * Indirect reference to a value. This class works together with the Java serialization mechanism in order to
 * reduce the required callstack depth during (de)serialization.
 */
// TODO: Move this class, as well as the other serialization utils to a tcommon subproject.
public final class Indirect<T> implements Externalizable {

    private T referent;

    /** Public no-arg constructor is required for serialization */
    public Indirect() {
        this(null);
    }

    private Indirect(T referent) {
        this.referent = referent;
    }

    /** Constructor. */
    public static <T> Indirect<T> of(T referent) {
        return new Indirect<>(referent);
    }

    /** Returns the object that this reference points to. */
    public T get() {
        return referent;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        LuaSerializer ls = LuaSerializer.getCurrent();
        if (ls != null) {
            ls.writeDelayed(referent);
        } else {
            out.writeObject(referent);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        LuaSerializer ls = LuaSerializer.getCurrent();
        if (ls != null) {
            ls.readDelayed(new DelayedReader() {
                @Override
                public void onRead(Object obj) {
                    referent = (T)obj;
                }
            });
        } else {
            referent = (T)in.readObject();
        }
    }

}
