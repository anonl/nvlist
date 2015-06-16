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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import nl.weeaboo.lua2.io.LuaSerializable;

/**
 * Upvalue used with Closure formulation
 * <p>
 * 
 * @see LuaClosure
 * @see Prototype
 */
@LuaSerializable
public final class UpValue implements Externalizable {

	private LuaValue sealed; //Gets set when closed
	private LuaValue[] array;
	private int index;

	/**
	 * Do not use. Required for efficient serialization. 
	 */
	@Deprecated
	public UpValue() {		
	}
	
	/**
	 * Create an upvalue relative to a stack
	 * 
	 * @param stack the stack
	 * @param index the index on the stack for the upvalue
	 */
	public UpValue(LuaValue[] stack, int index) {
		this.array = stack;
		this.index = index;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (sealed != null) {
			out.writeInt(-1);
			out.writeObject(sealed);
		} else {
			out.writeInt(index);
			out.writeObject(array);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		index = in.readInt();
		if (index < 0) {
			sealed = (LuaValue)in.readObject();
		} else {
			array = (LuaValue[])in.readObject();
		}
	}
	
	/**
	 * Convert this upvalue to a Java String
	 * 
	 * @return the Java String for this upvalue.
	 * @see LuaValue#tojstring()
	 */
	public String tojstring() {
		return getValue().tojstring();
	}

	/**
	 * Get the value of the upvalue
	 * 
	 * @return the {@link LuaValue} for this upvalue
	 */
	public final LuaValue getValue() {
		return (index < 0 ? sealed : array[index]);
	}

	/**
	 * Set the value of the upvalue
	 * 
	 * @param value the {@link LuaValue} to set it to
	 */
	public final void setValue(LuaValue value) {
		if (index < 0) {
			sealed = value;
		} else {
			array[index] = value;
		}
	}

	/**
	 * Close this upvalue so it is no longer on the stack
	 */
	public final void close() {
		if (sealed == null) {
			sealed = array[index];
			array = null;
			index = -1;
		}
	}

}
