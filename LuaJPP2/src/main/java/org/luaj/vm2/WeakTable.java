/*******************************************************************************
 * Copyright (c) 2009-2011 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import org.luaj.vm2.lib.TwoArgFunction;

import nl.weeaboo.lua2.io.LuaSerializable;

/**
 * Subclass of {@link LuaTable} that provides weak key and weak value semantics.
 * <p>
 * Normally these are not created directly, but indirectly when changing the
 * mode of a {@link LuaTable} as lua script executes.
 * <p>
 * However, calling the constructors directly when weak tables are required from
 * Java will reduce overhead.
 */
@LuaSerializable
public final class WeakTable extends LuaTable {

	private static final long serialVersionUID = -1298316583667381893L;

	//--- Uses manual serialization, don't add variables ---
	private transient boolean weakkeys, weakvalues;
	//--- Uses manual serialization, don't add variables ---

	public WeakTable() {
		this(false, false);
	}

	/**
	 * Construct a table with weak keys, weak values, or both
	 *
	 * @param weakkeys true to let the table have weak keys
	 * @param weakvalues true to let the table have weak values
	 */
	public WeakTable(boolean weakkeys, boolean weakvalues) {
		this(weakkeys, weakvalues, 0, 0);
	}

	/**
	 * Construct a table with weak keys, weak values, or both, and an initial
	 * capacity
	 *
	 * @param weakkeys true to let the table have weak keys
	 * @param weakvalues true to let the table have weak values
	 * @param narray capacity of array part
	 * @param nhash capacity of hash part
	 */
	protected WeakTable(boolean weakkeys, boolean weakvalues, int narray, int nhash) {
		super(narray, nhash);
		this.weakkeys = weakkeys;
		this.weakvalues = weakvalues;
	}

	/**
	 * Construct a table with weak keys, weak values, or both, and a source of
	 * initial data
	 *
	 * @param weakkeys true to let the table have weak keys
	 * @param weakvalues true to let the table have weak values
	 * @param source {@link LuaTable} containing the initial elements
	 */
	protected WeakTable(boolean weakkeys, boolean weakvalues, LuaTable source) {
		this(weakkeys, weakvalues, source.getArrayLength(), source.getHashLength());
		Varargs n;
		LuaValue k = NIL;
		while (!(k = ((n = source.next(k)).arg1())).isnil())
			rawset(k, n.arg(2));
		m_metatable = source.m_metatable;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		out.writeBoolean(weakkeys);
		out.writeBoolean(weakvalues);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);

		weakkeys = in.readBoolean();
		weakvalues = in.readBoolean();
	}

	@Override
	public void presize(int narray) {
		super.presize(narray);
	}

	/**
	 * Presize capacity of both array and hash parts.
	 *
	 * @param narray capacity of array part
	 * @param nhash capacity of hash part
	 */
	@Override
	public void presize(int narray, int nhash) {
		super.presize(narray, nhash);
	}

	@Override
	protected int getArrayLength() {
		return super.getArrayLength();
	}

	@Override
	protected int getHashLength() {
		return super.getHashLength();
	}

	@Override
	protected LuaTable changemode(boolean weakkeys, boolean weakvalues) {
		this.weakkeys = weakkeys;
		this.weakvalues = weakvalues;
		return this;
	}

	/**
	 * Self-sent message to convert a value to its weak counterpart
	 *
	 * @param value value to convert
	 * @return {@link LuaValue} that is a strong or weak reference, depending on
	 *         type of {@code value}
	 */
    static LuaValue weaken(LuaValue value) {
		switch (value.type()) {
		case LuaValue.TFUNCTION:
		case LuaValue.TTHREAD:
		case LuaValue.TTABLE:
			return new WeakValue(value);
		case LuaValue.TUSERDATA:
			return new WeakUserdata(value);
		default:
			return value;
		}
	}

	@Override
	public void rawset(int key, LuaValue value) {
		if (weakvalues) value = weaken(value);
		super.rawset(key, value);
	}

	@Override
	public void rawset(LuaValue key, LuaValue value) {
		if (weakvalues) value = weaken(value);
		if (weakkeys) {
			switch (key.type()) {
			case LuaValue.TFUNCTION:
			case LuaValue.TTHREAD:
			case LuaValue.TTABLE:
			case LuaValue.TUSERDATA:
				key = value = new WeakEntry(key, value);
				break;
			default:
				break;
			}
		}
		super.rawset(key, value);
	}

	@Override
	public LuaValue rawget(int key) {
		return super.rawget(key).strongvalue();
	}

	@Override
	public LuaValue rawget(LuaValue key) {
		return super.rawget(key).strongvalue();
	}

	/**
	 * Get the hash value for a key key the key to look up
	 * */
	@Override
	protected LuaValue hashget(LuaValue key) {
		if (hashEntries > 0) {
			int i = hashFindSlot(key);
			if (hashEntries == 0) return NIL;
			LuaValue v = hashValues[i];
			return v != null ? v : NIL;
		}
		return NIL;
	}

	// override to remove values for weak keys as we search
	@Override
	public int hashFindSlot(LuaValue key) {
		int i = (key.hashCode() & 0x7FFFFFFF) % hashKeys.length;
		LuaValue k;
		while ((k = hashKeys[i]) != null) {
			if (k.isweaknil()) {
				hashClearSlot(i);
				if (hashEntries == 0) return 0;
			} else {
				if (k.raweq(key)) return i;
				i = (i + 1) % hashKeys.length;
			}
		}
		return i;
	}

	@Override
	public int maxn() {
		return super.maxn();
	}

	/**
	 * Get the next element after a particular key in the table
	 *
	 * @return key,value or nil
	 */
	@Override
	public Varargs next(LuaValue key) {
		while (true) {
			Varargs n = super.next(key);
			LuaValue k = n.arg1();
			if (k.isnil()) return NIL;
			LuaValue ks = k.strongkey();
			LuaValue vs = n.arg(2).strongvalue();
			if (ks.isnil() || vs.isnil()) {
				super.rawset(ks, NIL);
			} else {
				return varargsOf(ks, vs);
			}
		}
	}

	// ----------------- sort support -----------------------------
	@Override
	public void sort(final LuaValue comparator) {
		@SuppressWarnings("serial")
		LuaFunction sortFunc = new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				return comparator.call(arg1.strongvalue(), arg2.strongvalue());
			}
		};
		super.sort(sortFunc);
	}

	/**
	 * Internal class to implement weak values.
	 *
	 * @see WeakTable
	 */
	@LuaSerializable
	static class WeakValue extends LuaValue implements Serializable {

		private static final long serialVersionUID = 1L;

		private transient WeakReference<LuaValue> ref;

		protected WeakValue(LuaValue value) {
			ref = new WeakReference<LuaValue>(value);
		}

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();

			LuaValue referent = (ref != null ? ref.get() : null);
			out.writeObject(referent);
		}

		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();

			LuaValue referent = (LuaValue)in.readObject();
			if (referent != null) {
				ref = new WeakReference<LuaValue>(referent);
			}
		}

		@Override
		public int type() {
			illegal("type", "weak value");
			return 0;
		}

		@Override
		public String typename() {
			illegal("typename", "weak value");
			return null;
		}

		@Override
		public String toString() {
			return "weak<" + getref() + ">";
		}

		@Override
		public LuaValue strongvalue() {
			Object o = getref();
			return o != null ? (LuaValue) o : NIL;
		}

		@Override
		public boolean raweq(LuaValue rhs) {
			Object o = getref();
			return o != null && rhs.raweq((LuaValue) o);
		}

		@Override
		public boolean isweaknil() {
			return getref() == null;
		}

		protected LuaValue getref() {
			return (ref != null ? ref.get() : null);
		}
	}

	/**
	 * Internal class to implement weak userdata values.
	 *
	 * @see WeakTable
	 */
	@LuaSerializable
	static final class WeakUserdata extends WeakValue {

		private static final long serialVersionUID = 1L;

		private transient WeakReference<Object> ob;
		private final LuaValue mt;

		private WeakUserdata(LuaValue value) {
			super(value);

			ob = new WeakReference<Object>(value.touserdata());
			mt = value.getmetatable();
		}

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();

			Object referent = (ob != null ? ob.get() : null);
			out.writeObject(referent);
		}

		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();

			Object referent = in.readObject();
			if (referent != null) {
				ob = new WeakReference<Object>(referent);
			}
		}

		@Override
		public LuaValue strongvalue() {
			Object u = getref();
			if (u != null) return (LuaValue) u;
			Object o = getob();
			return o != null ? userdataOf(o, mt) : NIL;
		}

		@Override
		public boolean raweq(LuaValue rhs) {
			if (!rhs.isuserdata()) return false;
			LuaValue v = getref();
			if (v != null && v.raweq(rhs)) return true;
			return rhs.touserdata() == getob();
		}

		@Override
		public boolean isweaknil() {
			return getob() == null || getref() == null;
		}

		protected Object getob() {
			return (ob != null ? ob.get() : null);
		}
	}

	/**
	 * Internal class to implement weak table entries.
	 *
	 * @see WeakTable
	 */
	@LuaSerializable
	static final class WeakEntry extends LuaValue implements Serializable {

		private static final long serialVersionUID = 1L;

		final LuaValue weakkey;
		LuaValue weakvalue;
		final int keyhash;

		private WeakEntry(LuaValue key, LuaValue weakvalue) {
            this.weakkey = weaken(key);
			this.keyhash = key.hashCode();
			this.weakvalue = weakvalue;
		}

		@Override
		public LuaValue strongkey() {
			return weakkey.strongvalue();
		}

		// when looking up the value, look in the keys metatable
		@Override
		public LuaValue strongvalue() {
			LuaValue key = weakkey.strongvalue();
			if (key.isnil()) return weakvalue = NIL;
			return weakvalue.strongvalue();
		}

		@Override
		public int type() {
			return TNONE;
		}

		@Override
		public String typename() {
			illegal("typename", "weak entry");
			return null;
		}

		@Override
		public String toString() {
			return "weak<" + weakkey.strongvalue() + "," + strongvalue() + ">";
		}

		@Override
		public int hashCode() {
			return keyhash;
		}

		@Override
		public boolean raweq(LuaValue rhs) {
			// return rhs.raweq(weakkey.strongvalue());
			return weakkey.raweq(rhs);
		}

		@Override
		public boolean isweaknil() {
			return weakkey.isweaknil() || weakvalue.isweaknil();
		}
	}
}
