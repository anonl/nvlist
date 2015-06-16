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
 * Prototype representing compiled lua code.
 * <p>
 * This is both a straight translation of the corresponding C type, and the main
 * data structure for execution of compiled lua bytecode.
 * <p>
 * See documentatation on {@link LuaClosure} for information on how to load and
 * execute a {@link Prototype}.
 * 
 * @see LuaClosure
 */
@LuaSerializable
public final class Prototype implements Externalizable {
	
	/* constants used by the function */
	public LuaValue[] k;
	public int[] code;
	/* functions defined inside the function */
	public Prototype[] p;
	/* map from opcodes to source lines */
	public int[] lineinfo;
	/* information about local variables */
	public LocVars[] locvars;
	/* upvalue names */
	public LuaString[] upvalues;
	public LuaString source;
	public int nups;
	public int linedefined;
	public int lastlinedefined;
	public int numparams;
	public int is_vararg;
	public int maxstacksize;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(k);
		out.writeObject(code);
		out.writeObject(p);
		out.writeObject(lineinfo);
		out.writeObject(locvars);
		out.writeObject(upvalues);
		out.writeObject(source);
		out.writeInt(nups);
		out.writeInt(linedefined);
		out.writeInt(lastlinedefined);
		out.writeInt(numparams);
		out.writeInt(is_vararg);
		out.writeInt(maxstacksize);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		k = (LuaValue[])in.readObject();
		code = (int[])in.readObject();
		p = (Prototype[])in.readObject();
		lineinfo = (int[])in.readObject();
		locvars = (LocVars[])in.readObject();
		upvalues = (LuaString[])in.readObject();
		source = (LuaString)in.readObject();
		nups = in.readInt();
		linedefined = in.readInt();
		lastlinedefined = in.readInt();
		numparams = in.readInt();
		is_vararg = in.readInt();
		maxstacksize = in.readInt();
	}
	
	@Override
	public String toString() {
		return source + ":" + linedefined + "-" + lastlinedefined;
	}

	/**
	 * Get the name of a local variable.
	 * 
	 * @param number the local variable number to look up
	 * @param pc the program counter
	 * @return the name, or null if not found
	 */
	public LuaString getlocalname(int number, int pc) {
		int i;
		for (i = 0; i < locvars.length && locvars[i].startpc <= pc; i++) {
			if (pc < locvars[i].endpc) { /* is variable active? */
				number--;
				if (number == 0) return locvars[i].varname;
			}
		}
		return null; /* not found */
	}

}
